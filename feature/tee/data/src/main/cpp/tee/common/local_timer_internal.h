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

#ifndef VERNIERDETECTOR_TEE_COMMON_LOCAL_TIMER_INTERNAL_H
#define VERNIERDETECTOR_TEE_COMMON_LOCAL_TIMER_INTERNAL_H

#include <string>

namespace verniertee::common::detail {

#if defined(__aarch64__)
    // Checks that CNTVCT_EL0 reads, converts to nanoseconds and advances. It is defined in
    // syscall_facade.cpp next to the counter reads it exercises, which stay in that translation
    // unit so they can be inlined into the timed reads there.
    bool arm64_cntvct_self_check(std::string *failure_reason);
#endif

}  // namespace verniertee::common::detail

#endif  // VERNIERDETECTOR_TEE_COMMON_LOCAL_TIMER_INTERNAL_H
