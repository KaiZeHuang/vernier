import bpy, math, os

OUT = r"F:\Vernier\assets"
GLB = os.path.join(OUT, "vernier-icon.glb")
BLEND = os.path.join(OUT, "vernier-icon.blend")
PNG = os.path.join(OUT, "vernier-icon-fallback.png")
HERO = os.path.join(OUT, "vernier-icon-hero.png")

bpy.ops.wm.read_factory_settings(use_empty=True)

# ---------------------------------------------------------------- geometry
def superellipse(a, b, n, res):
    pts = []
    for i in range(res):
        t = 2.0 * math.pi * i / res
        ct, st = math.cos(t), math.sin(t)
        e = 2.0 / n
        pts.append((math.copysign(abs(ct) ** e, ct) * a,
                    math.copysign(abs(st) ** e, st) * b))
    return pts

def pill(w, h, res=10):
    """true capsule: right semicircle then left semicircle"""
    r = min(w, h) / 2.0
    hw, hh = w / 2.0 - r, h / 2.0 - r
    pts = []
    for i in range(res + 1):                      # right cap: bottom -> top
        a = -math.pi / 2 + math.pi * i / res
        pts.append((hw + r * math.cos(a), hh + r * math.sin(a)))
    for i in range(res + 1):                      # left cap: top -> bottom
        a = math.pi / 2 + math.pi * i / res
        pts.append((-hw + r * math.cos(a), hh + r * math.sin(a)))
    return pts

def rounded_rect(w, h, r, res=8):
    """centered rounded rect (small radius -> crisp inlay marks)"""
    r = min(r, w / 2.0, h / 2.0)
    hw, hh = w / 2.0 - r, h / 2.0 - r
    pts = []
    for cx, cy, a0 in ((hw, -hh, -math.pi / 2), (hw, hh, 0.0), (-hw, hh, math.pi / 2), (-hw, -hh, math.pi)):
        for i in range(res + 1):
            a = a0 + math.pi / 2 * i / res
            pts.append((cx + r * math.cos(a), cy + r * math.sin(a)))
    return pts

def solid(name, pts, extrude, bevel, res):
    """curve -> mesh. NOTE: z span = 2*extrude + 2*bevel_depth (bevel bleeds into z)."""
    c = bpy.data.curves.new(name, 'CURVE')
    c.dimensions = '2D'
    c.fill_mode = 'BOTH'
    c.use_fill_caps = True
    c.extrude = extrude
    c.bevel_depth = bevel
    c.bevel_resolution = res
    sp = c.splines.new('POLY')
    sp.points.add(len(pts) - 1)
    for i, (x, y) in enumerate(pts):
        sp.points[i].co = (x, y, 0.0, 1.0)
    sp.use_cyclic_u = True
    o = bpy.data.objects.new(name, c)
    bpy.context.collection.objects.link(o)
    bpy.context.view_layer.objects.active = o
    o.select_set(True)
    bpy.ops.object.convert(target='MESH')
    m = bpy.context.view_layer.objects.active
    m.name = name
    bpy.ops.object.shade_smooth()
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)
    return m

def verts(m):
    bpy.context.view_layer.update()
    return [m.matrix_world @ v.co for v in m.data.vertices]

def zrange(m):
    vs = verts(m)
    return min(v.z for v in vs), max(v.z for v in vs)

def center_z(m):
    lo, hi = zrange(m)
    m.location.z -= (lo + hi) / 2.0
    bpy.ops.object.transform_apply(location=True, rotation=False, scale=False)
    return zrange(m)

def sit_on(m, z):
    """lift so the part's lowest vertex sits exactly at z"""
    lo, _ = zrange(m)
    m.location.z += z - lo
    bpy.ops.object.transform_apply(location=True, rotation=False, scale=False)
    return zrange(m)

def move_xy(m, x, y):
    m.location.x = x
    m.location.y = y
    bpy.ops.object.transform_apply(location=True, rotation=False, scale=False)

def material(name, base, metallic, rough, coat=0.0, coat_rough=0.25):
    mat = bpy.data.materials.new(name)
    mat.use_nodes = True
    bsdf = next((nd for nd in mat.node_tree.nodes if nd.type == 'BSDF_PRINCIPLED'), None)
    if bsdf is None:
        bsdf = mat.node_tree.nodes.new('ShaderNodeBsdfPrincipled')
    bsdf.inputs['Base Color'].default_value = (base[0], base[1], base[2], 1.0)
    bsdf.inputs['Metallic'].default_value = metallic
    bsdf.inputs['Roughness'].default_value = rough
    if 'Coat Weight' in bsdf.inputs:
        bsdf.inputs['Coat Weight'].default_value = coat
    if 'Coat Roughness' in bsdf.inputs:
        bsdf.inputs['Coat Roughness'].default_value = coat_rough
    return mat

MAT_INK = material("InkBody", (0.078, 0.074, 0.066), 0.10, 0.40, 0.42, 0.32)
MAT_CREAM = material("CreamInlay", (0.965, 0.950, 0.915), 0.0, 0.30, 0.35, 0.20)
MAT_ORANGE = material("AnodizedOrange", (0.860, 0.320, 0.020), 0.0, 0.28, 0.78, 0.28)

# ---------------------------------------------------------------- body
HALF = 0.92
BODY_EXTRUDE, BODY_BEVEL, BODY_RES = 0.145, 0.10, 10
body = solid("Body", superellipse(HALF, HALF, 4.2, 160), BODY_EXTRUDE, BODY_BEVEL, BODY_RES)
zlo, zhi = center_z(body)
body.data.materials.append(MAT_INK)
print("Body z=[%.3f, %.3f]" % (zlo, zhi))

# ---------------------------------------------------------------- marks (sit on the face)
# 游标卡尺符号：一条横尺 + 三个刻度齿骑在尺上沿 + 加粗滑动标横穿尺
GAP = 0.008
bar = solid("ScaleBar", rounded_rect(1.34, 0.115, 0.045, 8), 0.045, 0.014, 6)
bar.data.materials.append(MAT_CREAM)
sit_on(bar, zhi + GAP)
move_xy(bar, 0.0, -0.215)

for i, tx in enumerate((-0.46, 0.0, 0.46)):
    # 刻度齿：圆角矩形，从尺的上沿 (y=-0.157) 向上长到 y=0.08
    t = solid("Tick%d" % i, rounded_rect(0.105, 0.24, 0.030, 8), 0.050, 0.014, 6)
    t.data.materials.append(MAT_CREAM)
    sit_on(t, zhi + GAP)
    move_xy(t, tx, -0.038)

sld = solid("Slider", rounded_rect(0.19, 0.56, 0.095, 14), 0.095, 0.028, 7)
sld.data.materials.append(MAT_ORANGE)
sit_on(sld, zhi + GAP)
move_xy(sld, 0.19, -0.055)

for nm in ("ScaleBar", "Tick0", "Tick1", "Tick2", "Slider"):
    lo, hi = zrange(bpy.data.objects[nm])
    print("%-9s z=[%.3f, %.3f] (face %.3f)" % (nm, lo, hi, zhi))

# ---------------------------------------------------------------- render setup
scene = bpy.context.scene
scene.render.engine = 'BLENDER_EEVEE'
scene.render.film_transparent = True
scene.render.resolution_x = 512
scene.render.resolution_y = 512
scene.render.resolution_percentage = 100

world = bpy.data.worlds.new("Studio")
world.use_nodes = True
bg = world.node_tree.nodes.get("Background")
if bg:
    bg.inputs[0].default_value = (0.40, 0.42, 0.45, 1.0)
    bg.inputs[1].default_value = 0.40
scene.world = world

cam_data = bpy.data.cameras.new("Cam")
cam_data.lens = 52
cam = bpy.data.objects.new("Cam", cam_data)
bpy.context.collection.objects.link(cam)
cam.location = (0.26, -0.78, 2.78)
tgt = bpy.data.objects.new("CamTarget", None)
bpy.context.collection.objects.link(tgt)
con = cam.constraints.new('TRACK_TO')
con.target = tgt
con.track_axis = 'TRACK_NEGATIVE_Z'
con.up_axis = 'UP_Y'
scene.camera = cam

def area(name, loc, rot, size, energy, color):
    d = bpy.data.lights.new(name, 'AREA')
    d.size, d.energy, d.color = size, energy, color
    o = bpy.data.objects.new(name, d)
    bpy.context.collection.objects.link(o)
    o.location = loc
    o.rotation_euler = rot
    return o

area("Key", (2.4, -2.0, 3.0), (math.radians(-38), 0, math.radians(28)), 2.6, 150.0, (1.0, 0.95, 0.88))
area("Rim", (-2.4, 1.2, 1.4), (math.radians(-58), 0, math.radians(-32)), 2.2, 90.0, (1.0, 1.0, 1.0))
area("Fill", (-1.2, -2.4, 0.9), (math.radians(-28), 0, math.radians(-58)), 1.8, 55.0, (0.85, 0.90, 1.0))
area("Front", (0.15, -2.6, 2.1), (math.radians(-40), 0, math.radians(6)), 2.0, 120.0, (1.0, 1.0, 1.0))

# ---------------------------------------------------------------- export + render
bpy.ops.export_scene.gltf(filepath=GLB, export_format='GLB', export_materials='EXPORT', export_yup=True)
bpy.ops.wm.save_as_mainfile(filepath=BLEND)

scene.render.filepath = PNG
bpy.ops.render.render(write_still=True)
scene.render.resolution_x = 1024
scene.render.resolution_y = 1024
scene.render.filepath = HERO
bpy.ops.render.render(write_still=True)

print("EXPORTED", GLB, os.path.getsize(GLB))
print("RENDERED", PNG, os.path.getsize(PNG))
