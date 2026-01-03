# Precision-Draw-Card-Game

Overview

Precision Draw is a Java-based command-line strategic card game.
It simulates a two-player competitive game played over four rounds, where players aim to minimise their score by drawing cards as close as possible to a dynamically changing target.

The application focuses on algorithmic decision-making, custom data structure implementation, and clean modular design. All data structures are implemented from scratch using arrays, with no reliance on the Java Collections Framework.

*Some features listed below may be implemented incrementally as development progresses.*

Gameplay Rules

Two players compete in a match consisting of four rounds
The target value starts at 40 and may change between rounds based on player performance
A standard 52-card deck is used and reshuffled at the start of each round
The starting player is selected randomly for the first round and alternates thereafter

Card Values

Number cards (2–10): face value
Face cards (Jack, Queen, King): value of 10
Aces: automatically optimised as 1 or 11 to minimise score
Scoring
If total is less than or equal to the target: score = target − total
If total exceeds the target: score = 2 × (total − target)
If total equals the target exactly: score = −5
The player with the lowest cumulative score after four rounds wins the match

Menu Features

Play Match
View Leaderboard
Search Player History
Compare Two Players
List Players with More Than X Match Wins
Run Simulation
Exit
All player data is stored in memory for the duration of execution.

Simulation Mode

Simulates X matches between automatically generated players named SimPlayer1 to SimPlayer10
During each round:
The first player guesses a random number of cards between 3 and 7
The second player always guesses two more cards than the first
Supports matches between real players and simulated players

Extended Functionality

Strategy helper hints provided to the second player in each round
Robust input validation to prevent invalid or malformed input
Player analytics including:
Overshoot rate
Perfect hit count
Average number of cards guessed
Average round total
Best and worst match performance
These features enhance gameplay and provide deeper insight into player behaviour.

Algorithms and Data Structures

The application does not use the Java Collections Framework.
Custom data structures implemented using arrays:
MyArrayList<T> – dynamic array with automatic resizing
MyHashMapStringPlayer – hash map using open addressing with linear probing

Algorithms implemented:
Fisher–Yates shuffle for deck randomisation
Ace optimisation algorithm to minimise scoring difference
Manual insertion sort for leaderboard ranking

Testing and Validation

Incremental testing performed alongside development
Dedicated test classes covering:
Hand scoring and ace optimisation
Custom hash map collision handling and resizing
Deck creation and shuffle behaviour
Manual test cases used to validate:
Edge conditions
Invalid input handling
Simulation correctness
Match outcome accuracy

Development Approach

Built incrementally with frequent version control commits
Each feature implemented and validated before proceeding to the next
Emphasis placed on correctness, robustness, and explainable design

Notes

All data persistence is runtime-only
No Java Collections Framework classes are used
The codebase prioritises clarity, correctness, and maintainability
