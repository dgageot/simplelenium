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

public abstract class Check<T> {
  private final String description;
  private final String negatedDescription;

  public Check(String description, String negatedDescription) {
    this.description = description;
    this.negatedDescription = negatedDescription;
  }

  public String getDescription() {
    return description;
  }

  protected abstract Result execute(T element);

  public Check<T> negate() {
    return new Negated(this);
  }

  protected Result ok() {
    return new Result(true, description);
  }

  protected Result ok(String clue) {
    return new Result(true, clue);
  }

  protected Result ko(String clue) {
    return new Result(false, clue);
  }

  private static class Negated<T> extends Check<T> {
    private final Check<T> check;

    Negated(Check<T> check) {
      super(check.negatedDescription, check.description);
      this.check = check;
    }

    @Override
    protected Result execute(T element) {
      Result checkResult = check.execute(element);
      return new Result(!checkResult.isOk(), checkResult.message);
    }

    @Override
    public Check<T> negate() {
      return check;
    }
  }

  public static class Result {
    final String message;
    private final boolean success;

    private Result(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isOk() {
      return success;
    }
  }
}
