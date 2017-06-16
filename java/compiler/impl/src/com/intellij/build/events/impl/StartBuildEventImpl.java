/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.build.events.impl;

import com.intellij.build.events.StartBuildEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Vladislav.Soroka
 */
public class StartBuildEventImpl extends StartEventImpl implements StartBuildEvent {

  private final String myBuildTitle;

  public StartBuildEventImpl(@NotNull Object eventId,
                             @NotNull String buildTitle,
                             long eventTime,
                             @NotNull String message) {
    super(eventId, null, eventTime, message);
    myBuildTitle = buildTitle;
  }

  @Override
  public String getBuildTitle() {
    return myBuildTitle;
  }
}
