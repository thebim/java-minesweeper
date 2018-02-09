
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents a Minesweeper game.
 *
 * @author Timmy Iang (UGA ID #811478161)
 */
public class Minesweeper {

    /*
     * Variables
     */
    private final String MINE = " ";
    private final char BLANK_SPACE = ' ';
    private final char MARK_SYMBOL = 'F';
    private final char GUESS_SYMBOL = '?';

    private int rows, cols;
    private int userRow, userCol;
    private int roundsCompleted = 0;
    private int userScore;
    private int numMines;
    private boolean userWin = false;
    private String command;
    private String userCommand;
    private Object[][] gameGrid;
    private boolean[][] mineGrid;
    private boolean[][] markedGrid;
    private boolean[][] revealedGrid;
    private Scanner userInput = new Scanner(System.in);

    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * information provided in <code>seedFile</code>.
     *
     * @param seedFile the seed file used to construct the game
     */
    public Minesweeper(File seedFile) {
        try {
            Scanner genScan = new Scanner(seedFile);
            rows = genScan.nextInt();
            cols = genScan.nextInt();
            gameGrid = new Object[rows][cols];
            mineGrid = new boolean[rows][cols];
            markedGrid = new boolean[rows][cols];
            revealedGrid = new boolean[rows][cols];
            generateGrids();
            numMines = genScan.nextInt();

            if (numMines > (rows * cols)) {
                System.out.println("Cannot create game with " + seedFile + ", because it is not formatted correctly.");
                System.exit(0);
            }

            while (genScan.hasNext()) {
                int cellRow = genScan.nextInt();
                int cellCol = genScan.nextInt();
                if (cellRow < rows && cellCol < cols) {
                    if (isInBounds(cellRow, cellCol)) {
                        placeMine(cellRow, cellCol);
                    } else {
                        System.out.println("Cannot create game with " + seedFile + ", because it is not formatted correctly.");
                        System.exit(0);
                    }
                } else {
                    System.out.println("Cannot create game with " + seedFile + ", because it is not formatted correctly.");
                    System.exit(0);
                }
            }
            genScan.close();
        } catch (FileNotFoundException e) {}
    } // Minesweeper


    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * <code>rows</code> and <code>cols</code> values as the game grid's number
     * of rows and columns respectively. Additionally, One third (rounded up)
     * of the squares in the grid will be assigned mines, randomly.
     *
     * @param rows the number of rows in the game grid
     * @param cols the number of cols in the game grid
     */
    public Minesweeper(int rows, int cols) {
        if (rows > 10 || cols > 10) {
            System.out.println("Cannot create a mine field with that many rows and/or columns!");
            System.exit(0);
        }
        this.rows = rows;
        this.cols = cols;
        this.gameGrid = new Object[this.rows][this.cols];
        this.mineGrid = new boolean[this.rows][this.cols];
        this.markedGrid = new boolean[this.rows][this.cols];
        this.revealedGrid = new boolean[this.rows][this.cols];
        this.generateGrids();
        this.generateRandomMines();
    } // Minesweeper


    /**
     * Starts the game and execute the game loop.
     */
    public void run() {
        this.welcomeMessage();
        this.gameLoop();
    } // run


    /**
     * The entry point into the program. This main method does implement some
     * logic for handling command line arguments. If two integers are provided
     * as arguments, then a Minesweeper game is created and started with a
     * grid size corresponding to the integers provided and with 1/3 (rounded
     * up) of the squares containing mines, placed randomly. If a single word
     * string is provided as an argument then it is treated as a seed file and
     * a Minesweeper game is created and started using the information contained
     * in the seed file. If none of the above applies, then a usage statement
     * is displayed and the program exits gracefully.
     *
     * @param args the shell arguments provided to the program
     */
    public static void main(String[] args) {

	/*
	  The following switch statement has been designed in such a way that if
	  errors occur within the first two cases, the default case still gets
	  executed. This was accomplished by special placement of the break
	  statements.
	*/

        Minesweeper game = null;

        switch (args.length) {

            // random game
            case 2:

                int rows, cols;

                // try to parse the arguments and create a game
                try {
                    rows = Integer.parseInt(args[0]);
                    cols = Integer.parseInt(args[1]);
                    game = new Minesweeper(rows, cols);
                    break;
                } catch (NumberFormatException nfe) {
                    // line intentionally left blank
                } // try

                // seed file game
            case 1:

                String filename = args[0];
                File file = new File(filename);

                if (file.isFile()) {
                    game = new Minesweeper(file);
                    break;
                } // if

                // display usage statement
            default:

                System.out.println("Usage: java Minesweeper [FILE]");
                System.out.println("Usage: java Minesweeper [ROWS] [COLS]");
                System.exit(0);

        } // switch

        // if all is good, then run the game
        game.run();

    } // main


    /**
     * Prints the ASCII-art Minesweeper welcome message.
     */
    private void welcomeMessage() {
        System.out.println("\n    /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __");
        System.out.println("   /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|");
        System.out.println("  / /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |");
        System.out.println("  \\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|");
        System.out.println("                                       ALPHA |_| EDITION");
    }


    /**
     * Main loop for the game's functionality that prints out
     * the grid and prompts for user input.
     */
    private void gameLoop() {
        checkWin();
        getGameGrid();
        getUserInput();
    }


    /**
     * Populates the gameGrid 2D array with a blank space values for use
     * as a placeholder until the user starts 'mark-ing' the board. In addition,
     * it populates a grid of the same size with false values as to whether or not
     * a cell has a mine, is marked, or revealed.
     */
    private void generateGrids() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                gameGrid[i][j] = BLANK_SPACE;
                mineGrid[i][j] = false;
                markedGrid[i][j] = false;
                revealedGrid[i][j] = false;
            }
        }
    }


    /**
     * Places random mines around the game board based on the total
     * number of cells divided by 3.
     */
    private void generateRandomMines() {
        int randomNum1, randomNum2;
        int counter = 0;
        numMines = (rows * cols) / 3;
        while (counter <= numMines) {
            randomNum1 = (int) (Math.random() * rows);
            randomNum2 = (int) (Math.random() * cols);
            if (!hasMine(randomNum1, randomNum2)) {
                placeMine(randomNum1, randomNum2);
                counter++;
            }
        }
    }


    /**
     * Prints out the game grid.
     */
    private void getGameGrid() {
        System.out.println("\nRounds Completed: " + roundsCompleted + "\n");

        // loop for rows
        for (int y = 0; y < rows; y++) {
            System.out.print(y + " |");

            // loop for columns
            for (int x = 0; x < cols; x++) {
                System.out.print(" " + gameGrid[y][x] + " |");
            }
            System.out.print("\n"); // prints blank line for spacing
        }

        System.out.print("    ");

        // for loop that prints column numbers
        for (int x = 0; x < cols; x++) {
            System.out.print(x + "   ");
        }

        System.out.print("\n"); // prints blank line for spacing purposes
    }


    /**
     * Places a mine at the designated row/col combination
     * by changing the 'viewable' grid space to a mine
     * and assigning a true value to the mineGrid.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void placeMine(int row, int col) {
        gameGrid[row][col] = MINE;
        mineGrid[row][col] = true;
    }


    /**
     * Checks the mineGrid array to see if there is
     * a mine in the inputted row/col.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return a truth value based on if there is a mine or not
     */
    private boolean hasMine(int row, int col) {
        return mineGrid[row][col];
    }


    /**
     * Returns the number of mines adjacent to the specified
     * square in the grid.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     * @return the number of adjacent mines
     */
    private int getNumAdjMines(int row, int col) {
        int adjacentMines = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                try {
                    if (hasMine(i, j)) adjacentMines++;
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        }
        return adjacentMines;
    }


    /**
     * Prompts for user input and handles the functionality in
     * associating a command with its proper method.
     */
    private void getUserInput() {
        System.out.print("\nminesweeper-alpha$ ");
        command = userInput.nextLine();
        String commandTmp = command.trim().replaceAll("\\s{2,}", " ");  // Removes leading/trailing whitespace and extra spaces between words.

        String[] holder = commandTmp.split(" ");
        userCommand = holder[0];

        if (userCommand.equals("h") || userCommand.equals("help")) {
            helpMenu();
        } else if (userCommand.equals("q") || userCommand.equals("quit")) {
            endGame();
        } else if (holder.length > 2) {
            try {
                userRow = Integer.parseInt(holder[1]);
                userCol = Integer.parseInt(holder[2]);
            } catch (NumberFormatException nfe) {}

            switch(userCommand) {
                case "m":
                case "mark":
                    mark(userRow, userCol);
                    break;
                case "r":
                case "reveal":
                    reveal(userRow, userCol);
                    break;
                case "g":
                case "guess":
                    guess(userRow, userCol);
                    break;
                default:
                    System.out.println("Invalid command!");
                    roundsCompleted++;
                    gameLoop();
            }
        } else {
            System.out.println("Command not recognized!");
        }
        roundsCompleted++;
        gameLoop();
    }


    /**
     * Checks if the inputted values are within the bounds of the game
     * grid by returning false if the row/col value is greater than the
     * number of rows/cols in the game instance OR if the value is less
     * than zero.
     *
     * Parameters that should be passed are variables containing the
     * user's input.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private boolean isInBounds(int row, int col) {
        if ((row >= this.rows || row < 0) || (col >= this.cols || col < 0)) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Handles the functionality for the help menu.
     */
    private void helpMenu() {
        System.out.println("\nCommands Available...");
        System.out.println(" - Reveal: r/reveal row col");
        System.out.println(" -   Mark: m/mark   row col");
        System.out.println(" -  Guess: g/guess  row col");
        System.out.println(" -   Help: h/help");
        System.out.println(" -   Quit: q/quit");
        roundsCompleted++;
        getGameGrid();
        getUserInput();
    }


    /**
     * Marks the designated row/col combination with an 'F'
     * to denote that there is a mine at the location.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void mark(int row, int col) {
        if(isInBounds(row, col)) {
            gameGrid[row][col] = MARK_SYMBOL;
            markedGrid[row][col] = true;
        } else if (!isInBounds(row, col)) {
            System.out.println("\nYou tried to mark a square that does not exist!");
        }
        roundsCompleted++;
        gameLoop();
    }


    /**
     * Marks the designated row/col combination with a '?'
     * to denote that there is potentially a mine at the location.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void guess(int row, int col) {
        if(isInBounds(row, col)) {
            gameGrid[row][col] = GUESS_SYMBOL;
        } else if (!isInBounds(row, col)) {
            System.out.println("\nYou tried to guess a square that does not exist!");
        }
        roundsCompleted++;
        gameLoop();
    }


    /**
     * Reveals the cell at the row/col combination, and if
     * there is no mine, it will assign the number of adjacent
     * mines to the value of that cell on the gameGrid.
     *
     * @param row the row of the cell
     * @param col the column of the cell
     */
    private void reveal(int row, int col) {
        if(hasMine(row, col)) {
            loseByMine();
        } else if (isInBounds(row, col) && !mineGrid[row][col]) {
            gameGrid[row][col] = getNumAdjMines(row, col);
            revealedGrid[row][col] = true;
        } else if (!isInBounds(row, col)) {
            System.out.println("\nYou tried to reveal a square that does not exist!");
        }
    }

    /**
     * Checks the game board to see if all cells have been
     * marked -- and if all mines were marked and all other
     * cells were revealed, it calculates the user score and
     * displays a win message before closing.
     */
    private void checkWin() {
        boolean allExposed = true;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(gameGrid[i][j].equals(BLANK_SPACE) || gameGrid[i][j].equals(GUESS_SYMBOL) || gameGrid[i][j].equals(MINE)) {
                    allExposed = false;
                }

                if (mineGrid[i][j] == revealedGrid[i][j]) {
                    userWin = false;
                }

                if (markedGrid[i][j] == mineGrid[i][j]) {
                    userWin = true;
                }

            }
        }

        if(allExposed && userWin) {
            userScore = (rows * cols) - numMines - roundsCompleted;
            System.out.println("CONGRATULATIONS\n" +
                    "YOU HAVE WON!\n" +
                    "SCORE: " + userScore);
            userInput.close();
            System.exit(0);
        }
    }

    /**
     * Ends the game (used for user-quitting)
     */
    private void endGame() {
        System.out.println("Y U NO PLAY MORE?\n"
                            + "Bye!");
        userInput.close();
        System.exit(0);
    }


    /**
     * The message displayed when the user reveals
     * a mine. Ends the program.
     */
    private void loseByMine() {
        System.out.println("\n    Oh no... You revealed a mine!");
        System.out.println("    __ _  __ _ _ __ ___   ___    _____   _____ _ __");
        System.out.println("   / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|");
        System.out.println("  | (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ | ");
        System.out.println("   \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|  ");
        System.out.println("   |___/ ");
        System.exit(0);
    }

} // Minesweeper