/**
 * Copyright (C) 2013-2015 all@code-story.net
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
package net.codestory.simplelenium.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SeleniumRule implements TestRule {
  private final PrintTestName printTestName;
  private final InjectPageObjects injectPageObjects;
  private final TakeSnapshot takeSnapshot;
  private final PrintErrorConsole printErrorConsole;

  public SeleniumRule(Object target) {
    this.printTestName = new PrintTestName();
    this.injectPageObjects = new InjectPageObjects(target);
    this.takeSnapshot = new TakeSnapshot();
    this.printErrorConsole = new PrintErrorConsole();
  }

  @Override
  public Statement apply(Statement base, Description description) {
    base = takeSnapshot.apply(base, description);
    base = printErrorConsole.apply(base, description);
    base = injectPageObjects.apply(base, description);
    base = printTestName.apply(base, description);

    return base;
  }

  public void takeSnapshot() {
    takeSnapshot.takeSnapshot();
  }
}
