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

#pragma once

#include "preload/early_detector.h"
#include "preload/virtualization_early_detector.h"

#include <jni.h>

namespace vernier::preload {

    // The extras that carry the early mount evidence to the activity the launcher starts; the
    // activity hands them to Vernier.captureLaunchEvidence, which reads them by these keys.
    void attach_preload_extras(
            JNIEnv *env,
            jobject intent,
            jclass intentClass,
            const EarlyMountPreloadResult &result
    );

    // The extras that carry the early virtualization evidence on the same intent.
    void attach_virtualization_preload_extras(
            JNIEnv *env,
            jobject intent,
            jclass intentClass,
            const virtualization::EarlyVirtualizationResult &result
    );

}  // namespace vernier::preload
