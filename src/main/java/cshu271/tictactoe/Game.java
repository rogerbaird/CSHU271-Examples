package cshu271.tictactoe;

public class Game
{

	public enum Player
	{
		X, O, NONE
	};

	public enum Status
	{
		PENDING, STARTED, FINISHED
	};

	private Integer gameId;
	private Integer xPlayer;
	private Integer oPlayer;
	private Player winner = Player.NONE;
	private Player turn;
	private Status status;

	private Player[] board;

	public Game()
	{
		board = new Player[9];
		for (int i = 0; i < board.length; i++)
		{
			board[i] = Player.NONE;
		}
	}

	public synchronized void move(int userId, int row, int column)
	{
		checkTurn(userId);

		Integer index = row * 3 + column;
		checkAvailable(index);

		if (turn == Player.X)
		{
			board[index] = Player.X;
			turn = Player.O;
		} else
		{
			board[index] = Player.O;
			turn = Player.X;
		}

		checkWinner();
	}

	public void checkWinner()
	{
		Integer[][] data =
		{
			{
				0, 1, 2
			},
			{
				3, 4, 5
			},
			{
				6, 7, 8
			},
			{
				0, 3, 6
			},
			{
				1, 4, 7
			},
			{
				2, 5, 8
			},
			{
				0, 4, 8
			},
			{
				2, 4, 6
			},
		};

		for (Integer[] values : data)
		{
			if (board[values[0]] != Player.NONE
				&& board[values[0]] == board[values[1]]
				&& board[values[0]] == board[values[2]])
			{
				status = Status.FINISHED;
				turn = Player.NONE;
				if (board[values[0]] == Player.X)
				{
					winner = Player.X;
				} else
				{
					winner = Player.O;
				}
				return;
			}
		}

		for (Player c : board)
		{
			if (c == Player.NONE)
			{
				return;
			}
		}

		status = Status.FINISHED;
		turn = Player.NONE;
		winner = Player.NONE;
	}

	public Integer getGameId()
	{
		return gameId;
	}

	public void setGameId(int gameId)
	{
		this.gameId = gameId;
	}

	public Integer getXPlayer()
	{
		return xPlayer;
	}

	public void setXPlayer(int xPlayer)
	{
		this.xPlayer = xPlayer;
	}

	public Integer getOPlayer()
	{
		return oPlayer;
	}

	public void setOPlayer(int oPlayer)
	{
		this.oPlayer = oPlayer;
	}

	public Player getTurn()
	{
		return turn;
	}

	public void setTurn(Player turn)
	{
		this.turn = turn;
	}

	public Player[] getBoard()
	{
		return board;
	}

	public void setBoard(Player[] board)
	{
		this.board = board;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public Player getWinner()
	{
		return winner;
	}

	public Player getContents(Integer row, Integer column)
		throws IllegalArgumentException
	{
		if (row >= 0 && row <= 2 && column >= 0 && column <= 2)
		{
			return board[row * 3 + column];
		}

		throw new IllegalArgumentException();
	}

	private void checkAvailable(Integer index)
		throws IllegalArgumentException
	{
		if (board[index] != Player.NONE)
		{
			throw new IllegalArgumentException();
		}
	}

	private void checkTurn(Integer userId)
		throws IllegalArgumentException
	{
		if (turn == Player.X)
		{
			if (xPlayer != userId)
			{
				throw new IllegalArgumentException();
			}
		} else
		{
			if (oPlayer != userId)
			{
				throw new IllegalArgumentException();
			}
		}
	}
}
