import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BattleShip {

    // Grid size for the game
    static final int GRID_SIZE = 10;

    //symbols used to indicate ships, hits, misses, and water
    static final char water = '~';
    static final char ship = '#';
    static final char hit = 'X';
    static final char miss = 'O';
    static final String RED = "\u001B[31m";
    static final String GREEN = "\u001B[32m";
    static final String BLUE = "\u001B[34m";
    static final String RESET = "\u001B[0m";

    //Regex for checking the input
    static Pattern pattern = Pattern.compile("([0-9])([a-jA-J])|([a-jA-J])([0-9])");


    // Player 1's main grid containing their ships
    static char[][] player1Grid = new char[GRID_SIZE][GRID_SIZE];

    // Player 2's main grid containing their ships
    static char[][] player2Grid = new char[GRID_SIZE][GRID_SIZE];

    // Player 1's tracking grid to see their hits and misses
    static char[][] player1TrackingGrid = new char[GRID_SIZE][GRID_SIZE];

    // Player 2's tracking grid to see their hits and misses
    static char[][] player2TrackingGrid = new char[GRID_SIZE][GRID_SIZE];

    // Scanner object for user input
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        //Initialize game with some welcome texts and player names
        System.out.println("Welcome to Battle Ship!");
        System.out.print("Enter first player's name: ");
        String player1 = scanner.nextLine();
        System.out.print("Enter second player's name: ");
        String player2 = scanner.nextLine();

        // Initialize grids for both players
        initializeGrid(player1Grid);
        initializeGrid(player2Grid);
        initializeGrid(player1TrackingGrid);
        initializeGrid(player2TrackingGrid);

        // Place ships randomly on each player's grid
        placeShips(player1Grid);
        placeShips(player2Grid);

        // Variable to track whose turn it is
        boolean player1Turn = true;

        // Main game loop, runs until one player's ships are all sunk
        while (!isGameOver()) {
            if (player1Turn) {
                System.out.println(player1 + "'s turn");
                printGrid(player1TrackingGrid);
                playerTurn(player1, player2Grid, player1TrackingGrid);
            } else {
                System.out.println(player2 + "'s turn:");
                printGrid(player2TrackingGrid);
                playerTurn(player2, player1Grid, player2TrackingGrid);
            }
            player1Turn = !player1Turn;
        }

        System.out.println(GREEN + "Game Over!" + RESET);
        winnerWinnerChickenDinner(player1, player2 , player1Grid, player2Grid);
    }

    // Initializes a grid by filling it with water '~'
    static void initializeGrid(char[][] grid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                grid[i][j] = water;
            }
        }
    }

    // Places ships on a player's grid randomly
    static void placeShips(char[][] grid) {
        for (int shipSize = 5; shipSize >= 2; shipSize--) {
            boolean shipPlaced = false;
            while (!shipPlaced) {
                int x = (int) (Math.random() * 10);
                int y = (int) (Math.random() * 10);
                Random random = new Random();
                boolean horizontal = random.nextBoolean();
                if (canPlaceShip(grid, x, y, shipSize, horizontal)) {
                    for (int i = 0; i < shipSize; i++) {
                        if (horizontal) {
                            grid[i + x][y] = ship;
                        } else {
                            grid[x][y + i] = ship;
                        }
                    }
                    shipPlaced = true;
                }
            }
        }
    }

    // Checks if a ship can be placed at the specified location
    static boolean canPlaceShip(char[][] grid, int row, int col, int size, boolean horizontal) {
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) return false;
        else if (horizontal) {
            for (int i = row; i < size + row; i++) {

                if (grid[i][col] == '#' || row + i >= GRID_SIZE) return false;
            }
        } else {
            for (int i = col; i < size + col; i++) {
                if (grid[row][i] == '#' || col + i >= GRID_SIZE) return false;
            }
        }
        return true;
    }

    // Manages a player's turn, allowing them to attack the opponent's grid
    static void playerTurn(String name ,char[][] opponentGrid, char[][] trackingGrid) {
        System.out.println(name + ", enter your coordinates: (e.g, A5)");
        String input = scanner.nextLine();
        if (isValidInput(input, trackingGrid)) {
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                char letter = matcher.group(2) != null ? matcher.group(2).charAt(0) : matcher.group(3).charAt(0);
                char number = matcher.group(1) != null ? matcher.group(1).charAt(0) : matcher.group(4).charAt(0);
                int row = number - '0';
                int col = Character.toUpperCase(letter) - 'A';
                if (opponentGrid[row][col] != ship) {
                    trackingGrid[row][col] = miss;
                    System.out.println(RED + "You missed!" + RESET);
                } else {
                    trackingGrid[row][col] = hit;
                    opponentGrid[row][col] = water;
                    System.out.println(GREEN + "You hit!" + RESET);
                }
            }
        } else System.out.println(RED + "Invalid coordinates!" + RESET);
    }

    // Checks if the game is over by verifying if all ships are sunk
    static boolean isGameOver() {
        return allShipsSunk(player1Grid) || allShipsSunk(player2Grid);
    }

    // Checks if all ships have been destroyed on a grid
    static boolean allShipsSunk(char[][] grid) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == ship) return false;
            }
        }
        return true;
    }

    // Validates if the user input is in the correct format (e.g., A5)
    static boolean isValidInput(String input, char[][] grid) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) return false;
        else {
            char letter = matcher.group(2) != null ? matcher.group(2).charAt(0) : matcher.group(3).charAt(0);
            char number = matcher.group(1) != null ? matcher.group(1).charAt(0) : matcher.group(4).charAt(0);
            int row = number - '0';
            int col = Character.toUpperCase(letter) - 'A';
            return (row >= 0) && (row < GRID_SIZE) && (col >= 0) && (col < GRID_SIZE) && ((grid[row][col] == ship) || (grid[row][col] == water));
        }
    }

    // Prints the current state of the player's tracking grid
    static void printGrid(char[][] grid) {
        System.out.print("  ");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print((char) (i + 'A'));
            System.out.print(' ');
        }
        System.out.println();
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print((i));
            System.out.print(' ');
            for (int j = 0; j < GRID_SIZE; j++) {
                switch (grid[i][j]) {
                    case miss :
                        System.out.print(RED + grid[i][j] + RESET);
                        break;
                        case water :
                            System.out.print(BLUE + grid[i][j] + RESET);
                            break;
                            case hit :
                                System.out.print(GREEN + grid[i][j] + RESET);
                                break;
                }
                System.out.print(' ');
            }
            System.out.print(i);
            System.out.println();
        }
        System.out.print("  ");
        for (int i = 0; i < GRID_SIZE; i++) {
            System.out.print((char) (i + 'A'));
            System.out.print(' ');
        }
        System.out.println();
    }

    static void winnerWinnerChickenDinner(String player1, String player2, char[][] grid1, char[][] grid2) {
        if (allShipsSunk(grid1)) System.out.println(player2 + " wins!");
        if (allShipsSunk(grid2)) System.out.println(player1 + " wins!");
    }
}