/*
 * Copyright 2026 KaiZeHuang
 * If you have any questions, suggestions, or other inquiries, please open an issue at https://github.com/KaiZeHuang/vernier/issues.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef VERNIERDETECTOR_NATIVEROOT_PROBES_PERMISSION_BOUNDARY_PROBE_H
#define VERNIERDETECTOR_NATIVEROOT_PROBES_PERMISSION_BOUNDARY_PROBE_H

#include "nativeroot/common/types.h"

namespace vernier::nativeroot {

    // Set in ProbeResult::aux_flags when a netlink request that the restriction
    // applies to got an answer: an EACCES denial or a dump. Checks that were not
    // applicable or failed before the kernel decided leave it unset.
    constexpr long kBoundaryAuxEvaluated = 1L << 0;

    ProbeResult run_permission_boundary_check();

}  // namespace vernier::nativeroot

#endif  // VERNIERDETECTOR_NATIVEROOT_PROBES_PERMISSION_BOUNDARY_PROBE_H
