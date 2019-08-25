package cshu271.tictactoe;

import com.google.gson.Gson;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class JsonResponseBuilder
{
	public static Gson gson = new Gson();
	
	public static Response build(Object obj)
	{
		Response.ResponseBuilder builder = Response.status(Status.OK);
		builder.entity(gson.toJson(obj));
		return builder.build();
	}
	
	public static Response build()
	{
		return Response.status(Status.OK).build();
	}
}
