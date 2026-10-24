package com.kaizehuang.vernier.features.nativeroot.data.probes

import com.kaizehuang.vernier.features.nativeroot.data.native.CgroupProcessLeakNativeEntry

internal fun CgroupProcessLeakNativeEntry.describe(): String {
    return buildString {
        append("\nuidPath=")
        append(uidPath)
        append("\npid=")
        append(pid)
        append("\ncgroupUid=")
        append(cgroupUid)
        append("\nprocUid=")
        append(procUid ?: -1)
        startTimeTicks?.let {
            append("\nstartTimeTicks=")
            append(it)
        }
        killErrno?.let {
            append("\nkillErrno=")
            append(it)
        }
        sid?.let {
            append("\ngetsid=")
            append(it)
        }
        sidErrno?.let {
            append("\ngetsidErrno=")
            append(it)
        }
        pgid?.let {
            append("\ngetpgid=")
            append(it)
        }
        pgidErrno?.let {
            append("\ngetpgidErrno=")
            append(it)
        }
        schedulerPolicy?.let {
            append("\nschedulerPolicy=")
            append(it)
        }
        schedulerErrno?.let {
            append("\nschedulerErrno=")
            append(it)
        }
        pidfdErrno?.let {
            append("\npidfdErrno=")
            append(it)
        }
        if (procContext.isNotBlank()) {
            append("\nprocContext=")
            append(procContext)
        }
        if (comm.isNotBlank()) {
            append("\ncomm=")
            append(comm)
        }
        if (cmdline.isNotBlank()) {
            append("\ncmdline=")
            append(cmdline.replace('\u0000', ' '))
        }
    }
}

internal fun CgroupProcessLeakNativeEntry.liveness(): CgroupProcessLiveness {
    val evidence = buildList {
        if (pidfdErrno == 0) {
            add("pidfd_open")
        }
        when (killErrno) {
            0 -> add("kill(0)=0")
            ERRNO_PERMISSION_DENIED -> add("kill(0)=EPERM")
        }
        if (sidErrno == 0 && sid != null) {
            add("getsid=$sid")
        }
        if (pgidErrno == 0 && pgid != null) {
            add("getpgid=$pgid")
        }
        when {
            schedulerErrno == 0 && schedulerPolicy != null ->
                add("sched_getscheduler=$schedulerPolicy")

            schedulerErrno == ERRNO_PERMISSION_DENIED ->
                add("sched_getscheduler=EPERM")
        }
    }
    return CgroupProcessLiveness(
        confirmed = evidence.isNotEmpty(),
        evidence = evidence,
    )
}

internal fun CgroupProcessLeakNativeEntry.livenessDetail(
    liveness: CgroupProcessLiveness,
): String {
    return buildString {
        append("\nLiveness: ")
        if (liveness.confirmed) {
            append(liveness.evidence.joinToString())
        } else {
            append("unconfirmed")
        }
    }
}

internal const val ERRNO_PERMISSION_DENIED = 1
