# java-minesweeper
 
All mentioned commands must be called without the surrounding single-quote (').

Assuming you are in the main directory, one can compile the program by using:

      'javac -d classes/ src/Minesweeper.java'

After a successful compilation, the program may be ran two different ways:

  (1) Using a seed file to denote size of grid, number of mines, and location of the aforementioned mines. An example seed file is included to further clarify the format required.
  
      Use the command:
        'java -cp classes/ Minesweeper [file_path]'
        
  ...where [file_path] points to the seed file. The seed file will not work unless it is in a very specific format, which is as follows (without the '[]' brackets):
      
        [total_number_of_rows] [total_number_of_columns]
        [num of mines]
        [mine1_row] [mine1_column]
        [mine2_row] [mine2_column]
        
      ...and so on for however many mines were specified.
      
  (2) Denoting the size of the grid, and allowing the program to randomly place mines:
  
      Use the command:
        'java -cp /classes Minesweeper [total_number_of_rows] [total_number_of_columns]'
        
The game is played using commands via the console -- and one can access the full list of usable commands by typing 'h' or 'help' (without the quotes) at any point in the game.
