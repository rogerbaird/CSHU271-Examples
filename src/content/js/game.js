
var game = new Object();

game.private = new Object();

game.move = function (row, col) {
    if (!(game.private.state == undefined)) {
        var data = new Object;
        data.userId = game.private.userId;
        data.row = row;
        data.column = col;
        $.ajax({
            type: "POST",
            url: '/api/game/' + game.private.state.gameId,
            data: data,
            success: function (data) {
                game.updateGame();
            },
            dataType: null
        });
    }
}


game.initialize = function () {
    if (game.private.state == undefined) {
        game.clearBoard();
        $("#main_content").html("<br/><button id='game_control'>Start game</button>");
        $("#game_control").click(function () {
            $("#main_content").html("<div id='status'>Joining...</div>");
            game.joinGame();
        });
    }
}

game.clearBoard = function () {
    var canvas = document.getElementById("board");

    canvas.addEventListener('click', function (event) {
        var x = event.pageX - canvas.offsetLeft;
        var y = event.pageY - canvas.offsetTop;
        var row = Math.floor(y / 100);
        var col = Math.floor(x / 100);
        game.move(row, col);
    });

    var ctx = canvas.getContext("2d");
    ctx.lineWidth = 1;
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.beginPath();
    ctx.moveTo(100, 0);
    ctx.lineTo(100, 300);
    ctx.moveTo(200, 0);
    ctx.lineTo(200, 300);
    ctx.moveTo(0, 100);
    ctx.lineTo(300, 100);
    ctx.moveTo(0, 200);
    ctx.lineTo(300, 200);
    ctx.stroke();

}

game.drawWinner = function (board)
{
    var ctx = document.getElementById("board").getContext("2d");

    ctx.beginPath();

    if (board[0] != "NONE" && board[0] == board[1] && board[0] == board[2])
    {
        ctx.moveTo(10, 50);
        ctx.lineTo(290, 50);
    } else if (board[3] != "NONE" && board[3] == board[4] && board[3] == board[5])
    {
        ctx.moveTo(10, 150);
        ctx.lineTo(290, 150);
    }
    else if ( board[6] != "NONE" && board[6] == board[7] && board[6] == board[8])
    {
        ctx.moveTo(10, 250);
        ctx.lineTo(290, 250);
    }
    else if (board[0] != "NONE" && board[0] == board[3] && board[0] == board[6])
    {
        ctx.moveTo(50, 10);
        ctx.lineTo(50, 290);
    } else if (board[1] != "NONE" && board[1] == board[4] && board[1] == board[7])
    {
        ctx.moveTo(150, 10);
        ctx.lineTo(150, 290);
    }
    else if ( board[2] != "NONE" && board[2] == board[5] && board[2] == board[8])
    {
        ctx.moveTo(250, 10);
        ctx.lineTo(250, 290);
    } else if (board[0] != "NONE" && board[0] == board[4] && board[0] == board[8])
    {
        ctx.moveTo(10, 10);
        ctx.lineTo(290, 290);
    }
    else if ( board[2] != "NONE" && board[2] == board[4] && board[2] == board[6])
    {
        ctx.moveTo(290, 10);
        ctx.lineTo(10, 290);
    }
    

    var oldSS = ctx.strokeStyle;
    var oldLineWidth = ctx.lineWidth;
    ctx.strokeStyle = '#ff0000';
    ctx.lineWidth = 10;
    ctx.stroke();
    ctx.strokeStyle = oldSS;
    ctx.lineWidth = oldLineWidth;
}

game.drawBoard = function (board) {

    var ctx = document.getElementById("board").getContext("2d");

    ctx.lineWidth = 5;
    var row;
    var col;
    for (row = 0; row <= 2; row++)
    {
        for (col = 0; col <= 2; col++)
        {
            if (board[col * 3 + row] == 'X')
            {
                ctx.beginPath();
                ctx.moveTo(row * 100 + 10, col * 100 + 10);
                ctx.lineTo(row * 100 + 90, col * 100 + 90);
                ctx.moveTo(row * 100 + 90, col * 100 + 10);
                ctx.lineTo(row * 100 + 10, col * 100 + 90);
                ctx.stroke();
            } else if (board[col * 3 + row] == 'O')
            {
                ctx.beginPath();
                ctx.arc(row * 100 + 50, col * 100 + 50, 45, 2 * Math.PI, false);
                ctx.stroke();
            }
        }
    }
}

game.updateGame = function () {
    $.get("/api/game/" + game.private.state.gameId, function (data) {
        game.private.state = data;

        game.drawBoard(data.board);

        if (data.status == "PENDING")
        {
            $("#main_content").html("<div id='status'>Waiting for another player...</div>");
        } else if (data.status == "STARTED")
        {
            if (game.private.userId == data.xPlayer)
            {
                if (data.turn == 'X')
                {
                    $("#main_content").html("<div id='status'>Your turn, X...</div>");
                } else
                {
                    $("#main_content").html("<div id='status'>Waiting for O...</div>");
                }
            } else
            {
                if (data.turn == 'O')
                {
                    $("#main_content").html("<div id='status'>Your turn, O...</div>");
                } else
                {
                    $("#main_content").html("<div id='status'>Waiting for X...</div>");
                }
            }
        } else if (data.status == "FINISHED")
        {
            game.drawWinner(data.board);

            if (data.winner == 'X')
            {
                $("#main_content").html("<div id='status'>X is the winner!</div><br/><button id='game_control'>Play again?</button>");
            } else if (data.winner == 'O')
            {
                $("#main_content").html("<div id='status'>O is the winner!</div><br/><button id='game_control'>Play again?</button");
            } else
            {
                $("#main_content").html("<div id='status'>It's a draw!</div><br/><button id='game_control'>Play again?</button");
            }
            $('#game_control').click(game.joinGame);
        }
    });
};

game.joinGame = function () {
    $.get("/api/join/" + game.private.userId, function (data) {
        game.private.state = data;
        game.clearBoard();
        setInterval(game.updateGame, 1000);
    });
};

game.registerUser = function () {
    $.get("/api/user", function (data) {
        game.private.userId = data;
    });
};

game.registerUser();

