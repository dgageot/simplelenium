/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium;

class Verification {
  private static final Verification NOT_FOUND = new Verification(null);
  private static final Verification OK = new Verification(null);

  static Verification ko() {
    return ko(null);
  }

  static Verification ko(String clue) {
    return new Verification(clue);
  }

  static Verification ok() {
    return OK;
  }

  static Verification notFound() {
    return NOT_FOUND;
  }

  private final String clue;

  private Verification(String clue) {
    this.clue = clue;
  }

  public boolean isOk() {
    return this == OK;
  }

  public boolean isNotFound() {
    return this == NOT_FOUND;
  }

  public String description(String verificationDesc) {
    final String message;
    if (isOk()) {
      message = "Verified that " + verificationDesc;
    } else if (isNotFound()) {
      message = "Element not found. Failed to verify that " + verificationDesc;
    } else {
      message = "Failed to verify that " + verificationDesc;
    }

    if (clue == null) {
      return message;
    }
    return message + ". " + clue;
  }
}
