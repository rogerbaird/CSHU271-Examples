
package cshu271.tictactoe;

import com.google.gson.Gson;
import cshu271.tictactoe.Game.Player;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/api")
public class TicTacToeResource
{

	private Gson gson = new Gson();
	private static final Map<Integer, Game> pendingGames = new HashMap();
	private static final Map<Integer, Game> startedGames = new HashMap();
	private static final Map<Integer, Game> finishedGames = new HashMap();
	private static Integer nextUserId = 1;
	private static Integer nextGameId = 1;

	/**
	 * Returns an Integer (user ID) that is a new unique identifier for a user to use in subsequent 
	 * calls.
	 * @return Response.status: Status.OK
	 *          Response.entity Integer (json)
	 */
	@GET
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNewUserId()
	{
		return Response.status(Status.OK).entity(gson.toJson(nextUserId++)).build();
	}

	/**
	 * Joins a user to a game, creating a game if necessary.  Returns the game ID.
	 * @param userId the userId to join to a game
	 * @return Response.status: Status.OK
	 *          Response.entity: Integer (json)
	 */
	@GET
	@Path("/join/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response join(@PathParam("userId") Integer userId)
	{
		Game game;
		synchronized (pendingGames)
		{
			if (pendingGames.isEmpty())
			{
				game = new Game();
				game.setStatus(Game.Status.PENDING);
				game.setGameId(nextGameId++);
				game.setXPlayer(userId);
				pendingGames.put(game.getGameId(), game);
			} else
			{
				game = pendingGames.entrySet().iterator().next().getValue();
				pendingGames.remove(game.getGameId());
				game.setOPlayer(userId);
				game.setTurn(Player.X);
				game.setStatus(Game.Status.STARTED);
				startedGames.put(game.getGameId(), game);
			}
		}
		return Response.status(Status.OK).entity(gson.toJson(game)).build();
	}

	/**
	 * Returns the state of a current game indicated by the gameId
	 * @param gameId the ID of the game to return the state for
	 * @return Response.status: Status.NOT_FOUND if the game ID is invalid
	 *          Response.status: Status.OK if the game ID is valid
	 *          Response.entity: Game (json)
	 */
	@GET
	@Path("/game/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response game(@PathParam("id") Integer gameId)
	{
		if (startedGames.containsKey(gameId))
		{
			return Response.status(Status.OK).entity(gson.toJson(startedGames.get(gameId))).build();
		}
		if (pendingGames.containsKey(gameId))
		{
			return Response.status(Status.OK).entity(gson.toJson(pendingGames.get(gameId))).build();
		}
		if (finishedGames.containsKey(gameId))
		{
			return Response.status(Status.OK).entity(gson.toJson(finishedGames.get(gameId))).build();
		}
		return Response.status(Status.NOT_FOUND).build();
	}

	/**
	 * Attempts to perform a move and returns the updated game state    
	 * @param gameId the game to apply the move to
	 * @param userId the user applying the move
	 * @param row the row 0-2 of the grid to apply the move to
	 * @param column the column 0-2 of the grid to apply the move to
	 * @return Response.status: Status.BAD_REQUEST if the row or column or move is invalid
	 *          Response.status: Status.NOT_FOUND if the game is not a valid started game
	 *          Response.status: Status.OK if the move is valid
	 *          Response.entity: Game (json) if the move is valid
	 */
	@POST
	@Path("/game/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response move(@PathParam("id") Integer gameId, 
		@FormParam("userId") Integer userId, 
		@FormParam("row") Integer row, 
		@FormParam("column") Integer column)
	{
		if (row == null || row < 0 || row > 2 || column == null || column < 0 || column > 2)
		{
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		synchronized (startedGames)
		{
			if (!startedGames.containsKey(gameId))
			{
				return Response.status(Status.NOT_FOUND).build();
			}
			
			Game game = startedGames.get(gameId);
			try
			{
				game.move(userId, row, column);
				if ( game.getStatus() == Game.Status.FINISHED)
				{
					finishedGames.put(game.getGameId(), game);
					startedGames.remove(game.getGameId());
				}
				return Response.status(Status.OK).entity(gson.toJson(game)).build();
			}
			catch(IllegalArgumentException iae)
			{
				return Response.status(Status.BAD_REQUEST).build();
			}
		}
	}
}
