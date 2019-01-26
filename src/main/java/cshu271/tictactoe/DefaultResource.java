
package cshu271.tictactoe;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class DefaultResource
{
    @GET 
    @Produces("text/plain")
    public Response getIt2() {
		
		try
		{
			return Response.seeOther(new URI("/web/index.html")).build();
		}
		catch (URISyntaxException ex)
		{
			Logger.getLogger(DefaultResource.class.getName()).log(Level.SEVERE, null, ex);
			return Response.serverError().build();
		}
    }


}