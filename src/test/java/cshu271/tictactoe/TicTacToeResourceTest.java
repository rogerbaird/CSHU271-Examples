package cshu271.tictactoe;

import com.google.gson.Gson;
import com.sun.jersey.api.client.ClientResponse.Status;
import cshu271.tictactoe.Game.Player;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicTacToeResourceTest
{

	private TicTacToeResource instance;
	private Gson gson;
	private Integer user1;
	private Integer user2;

	public TicTacToeResourceTest()
	{
	}

	@BeforeClass
	public static void setUpClass()
	{
	}

	@AfterClass
	public static void tearDownClass()
	{
	}

	@Before
	public void setUp()
	{
		instance = new TicTacToeResource();
		gson = new Gson();
	}

	@After
	public void tearDown()
	{
	}

	/**
	 * Test 1.1
	 *
	 * When start a new game, there will be a new empty 3*3 board where every
	 * valid cell should be empty.
	 */
	@Test
	public void test_1_1()
	{
		Game game = givenANewGame();
		expectBlankBoard(game);
	}

	/**
	 * Test 1.2
	 *
	 * When a board cell is referenced by a row index greater than 2, the cell
	 * is invalid
	 */
	@Test
	public void test_1_2()
	{
		Game game = givenANewGame();
		Response result = instance.move(game.getGameId(), user1, 3, 0);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
	}

	/**
	 * Test 1.3
	 *
	 * When a board cell is referenced by a column index greater than 2, the
	 * cell is invalid
	 */
	@Test
	public void test_1_3()
	{
		Game game = givenANewGame();
		Response result = instance.move(game.getGameId(), user1, 0, 3);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
	}

	/**
	 * Test 2.1
	 *
	 * Given a new board, then it should be X's turn.
	 */
	@Test
	public void test_2_1()
	{
		Game game = givenANewGame();
		assertEquals(Player.X, game.getTurn());
	}

	/**
	 * Test 3.1
	 *
	 * Given a board, it is X's turn, when user clicks on an empty cell, then X
	 * is placed in the cell, and turn is changed to O
	 */
	@Test
	public void test_3_1()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		game = gson.fromJson(result.getEntity().toString(), Game.class);

		try
		{
			assertEquals(Player.X, game.getContents(2, 2));
			assertEquals(Player.O, game.getTurn());
		} catch (IllegalArgumentException ex)
		{
			Assert.fail(ex.getMessage());
		}
	}
	
	/**
	 * Test 3.2
	 *
	 * Given a board, it is X's turn, when user clicks on a non-empty cell, then it is an illegal move, and turn is not changed
	 */
	@Test
	public void test_3_2()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 2);
		result = instance.move(game.getGameId(), game.getXPlayer(), 1, 2);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
		
		game = gson.fromJson(instance.game(game.getGameId()).getEntity().toString(), Game.class);
		assertEquals(Player.X, game.getTurn());
	}
	
	/**
	 * Test 3.3
	 *
	 * Given a board, it is X's turn, when user clicks on an illegal cell (outside the board), then  it is an illegal move and  turn is not changed
	 */
	@Test
	public void test_3_3()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 2);
		result = instance.move(game.getGameId(), game.getXPlayer(), 1, 4);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
		
		game = gson.fromJson(instance.game(game.getGameId()).getEntity().toString(), Game.class);
		assertEquals(Player.X, game.getTurn());
	}

	/**
	 * Test 4.1
	 *
	 * Given a  board, it is O's turn, when user clicks on an empty cell, then O is placed in the cell,  and turn is changed to X
	 */
	@Test
	public void test_4_1()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 2);
		game = getGame(result);
		assertEquals(Player.O, game.getContents(1, 2));
		assertEquals(Player.X, game.getTurn());
		
	}
	
	/**
	 * Test 4.2
	 *
	 * Given a board, it is O's turn, when user clicks on a non-empty cell, then it is an illegal move, and turn is not changed
	 */
	@Test
	public void test_4_2()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 2, 2);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
		game = getGame(game.getGameId());
		assertEquals(Player.O, game.getTurn());
	}
		
	/**
	 * Test 4.3
	 *
	 * Given a board, it is O's turn, when user clicks on an illegal cell (outside the board), then  it is an illegal move and  turn is not changed
	 */
	@Test
	public void test_4_3()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 4);
		assertEquals(Status.BAD_REQUEST.getStatusCode(), result.getStatus());
		game = getGame(game.getGameId());
		assertEquals(Player.O, game.getTurn());
	}
	
	/**
	 * Test 5.1
	 *
	 * Given a board when there is XXX then X won
	 */
	@Test
	public void test_5_1()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 0, 0);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 0);
		result = instance.move(game.getGameId(), game.getXPlayer(), 0, 1);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 1);
		result = instance.move(game.getGameId(), game.getXPlayer(), 0, 2);
		
		game = getGame(result);
		assertEquals(Game.Status.FINISHED, game.getStatus());
		assertEquals(Player.X, game.getWinner());
	
	}
	
	/**
	 * Test 5.2
	 *
	 * Given a board when there is OOO then O won
	 */
	@Test
	public void test_5_2()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 0, 0);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 0);
		result = instance.move(game.getGameId(), game.getXPlayer(), 0, 1);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 1);
		result = instance.move(game.getGameId(), game.getXPlayer(), 2, 0);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 2);
		
		game = getGame(result);
		assertEquals(Game.Status.FINISHED, game.getStatus());
		assertEquals(Player.O, game.getWinner());
	
	}
	
	/**
	 * Test 5.3
	 *
	 * Given a board when no cell is empty and there is no XXX or OOO, then it is a draw
	 */
	@Test
	public void test_5_3()
	{
		Game game = givenANewGame();

		Response result = instance.move(game.getGameId(), game.getXPlayer(), 0, 0);
		result = instance.move(game.getGameId(), game.getOPlayer(), 0, 2);
		result = instance.move(game.getGameId(), game.getXPlayer(), 0, 1);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 0);
		result = instance.move(game.getGameId(), game.getXPlayer(), 1, 2);
		result = instance.move(game.getGameId(), game.getOPlayer(), 1, 1);
		result = instance.move(game.getGameId(), game.getXPlayer(), 2, 0);
		result = instance.move(game.getGameId(), game.getOPlayer(), 2, 2);
		result = instance.move(game.getGameId(), game.getXPlayer(), 2, 1);
		
		game = getGame(result);
		for (Player p : game.getBoard())
			assertTrue(p != Player.NONE);
		
		assertEquals(Game.Status.FINISHED, game.getStatus());
		assertEquals(Player.NONE, game.getWinner());
	
	}

	private Game givenANewGame()
	{
		Integer user1 = registerUser();
		Integer user2 = registerUser();
		Game game1 = joinGame(user1);
		assertEquals(Game.Status.PENDING, game1.getStatus());
		Game game2 = joinGame(user2);
		assertEquals(Game.Status.STARTED, game2.getStatus());
		assertEquals(game1.getGameId(), game2.getGameId());
		assertEquals(user1, game2.getXPlayer());
		assertEquals(user2, game2.getOPlayer());
		return game2;
	}

	private void whenMoving(Game game, Integer userId, Integer row, Integer column)
	{
		game.move(userId, row, column);
	}

	private void expectBlankBoard(Game game)
	{
		for (Player p : game.getBoard())
			assertEquals(p, Player.NONE);
	}

	///
	private Integer registerUser()
	{
		Response result = instance.getNewUserId();
		assertEquals(Status.OK.getStatusCode(), result.getStatus());
		Integer userId = gson.fromJson(result.getEntity().toString(), Integer.class);
		assertTrue(userId > -1);
		return userId;
	}

	private Game joinGame(Integer userId)
	{
		Response result = instance.join(userId);
		assertEquals(Status.OK.getStatusCode(), result.getStatus());
		Game game = gson.fromJson(result.getEntity().toString(), Game.class);
		Assert.assertNotNull(game);
		return game;
	}
	
	private Game getGame(Integer gameId)
	{
		Response result = instance.game(gameId);
		return gson.fromJson(result.getEntity().toString(), Game.class);
	}
	
	private Game getGame(Response result)
	{
		return gson.fromJson(result.getEntity().toString(), Game.class);
	}

}
