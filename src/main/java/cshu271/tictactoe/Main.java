package cshu271.tictactoe;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import org.glassfish.grizzly.http.server.HttpServer;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;

public class Main
{

	private static int getPort(int defaultPort)
	{
		//grab port from environment, otherwise fall back to default port 9998
		String httpPort = System.getProperty("jersey.test.port");
		if (null != httpPort)
		{
			try
			{
				return Integer.parseInt(httpPort);
			} catch (NumberFormatException e)
			{
			}
		}
		return defaultPort;
	}

	private static URI getBaseURI()
	{
		return UriBuilder.fromUri("http://0.0.0.0/").port(getPort(9998)).build();
	}

	public static final URI BASE_URI = getBaseURI();

	protected static HttpServer startServer() throws IOException
	{
		ResourceConfig resourceConfig = new PackagesResourceConfig("cshu271.tictactoe");

		System.out.println("Starting grizzly2...");
		return GrizzlyServerFactory.createHttpServer(BASE_URI, resourceConfig);
	}

	public static void main(String[] args) throws IOException
	{
		// Grizzly 2 initialization
		HttpServer httpServer = startServer();
		CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(Main.class.getClassLoader());
		httpServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/web");
		System.out.println(String.format("App started at http://localhost:9998/web/index.html"
			+ "\nHit enter to stop it...",
			BASE_URI));
		System.in.read();
		httpServer.stop();
	}
}
