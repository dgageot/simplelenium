package net.codestory.simplelenium.driver;

import com.google.common.base.Throwables;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.service.DriverService;

import java.io.IOException;

import static java.util.Collections.singletonMap;
import static org.openqa.selenium.remote.DriverCommand.NEW_SESSION;
import static org.openqa.selenium.remote.DriverCommand.QUIT;
import static org.openqa.selenium.remote.HttpVerb.POST;

class PhantomJSHttpCommandExecutor extends HttpCommandExecutor {
  private final DriverService service;

  public PhantomJSHttpCommandExecutor(DriverService service) {
    super(singletonMap("executePhantomScript", new CommandInfo("/session/:sessionId/phantom/execute", POST)), service.getUrl());
    this.service = service;
  }

  @Override
  public Response execute(Command command) throws IOException {
    if (NEW_SESSION.equals(command.getName())) {
      service.start();
    }

    try {
      return super.execute(command);
    } catch (Throwable t) {
      Throwables.propagateIfPossible(t);
      throw new WebDriverException(t);
    } finally {
      if (QUIT.equals(command.getName())) {
        service.stop();
      }
    }
  }
}

