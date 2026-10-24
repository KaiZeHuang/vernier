# Payload helpers every unit shares. libmain.so compiles its own copy of these sources.
set(VERNIERDETECTOR_COMMON_SOURCES ${CMAKE_CURRENT_SOURCE_DIR}/payload_codec.cpp PARENT_SCOPE)

vernier_native_unit(vernier_common
    disposable_child.cpp
    payload_codec.cpp
)
