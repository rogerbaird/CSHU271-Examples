package cshu271.tictactoe;

import com.google.gson.Gson;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ErrorResponseBuilder
{
	public static Gson gson = new Gson();
	
	public static Response build(Status status)
	{
		return Response.status(status).build();
	}
	
	public static Response build(Status status, Object entity)
	{
		return Response
			.status(status)
			.entity(gson.toJson(entity))
			.build();
	}
}
