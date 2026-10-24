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

#ifndef VERNIERDETECTOR_NATIVEROOT_PROBES_DEVPTS_PERMISSION_CHECK_PROBE_H
#define VERNIERDETECTOR_NATIVEROOT_PROBES_DEVPTS_PERMISSION_CHECK_PROBE_H

#include "nativeroot/common/types.h"

namespace vernier::nativeroot {

    ProbeResult run_devpts_permission_check();

}  // namespace vernier::nativeroot

#endif  // #ifdef VERNIERDETECTOR_NATIVEROOT_PROBES_KERNELPATCH_SUPERCALL_LATENCY_PROBE_H