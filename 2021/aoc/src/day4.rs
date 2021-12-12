use std::collections::HashMap;
use std::collections::HashSet;

#[derive(Hash, Eq, PartialEq, Debug, Clone, Copy)]
struct Point(usize, usize);

#[derive(Debug)]
struct Board {
    points_by_number: HashMap<i32, Point>,
    marks: HashSet<Point>,
}

impl Board {
    fn new() -> Board {
        Board {
            points_by_number: HashMap::new(),
            marks: HashSet::new(),
        }
    }

    fn is_win(&self, win_conditions: &[Vec<Point>]) -> bool {
        return win_conditions
            .iter()
            .any(|win_condition| win_condition.iter().all(|x| self.marks.contains(x)));
    }

    fn sum_unmarked(&self) -> i32 {
        return self
            .points_by_number
            .iter()
            .fold(0, |sum, (number, point)| {
                if !self.marks.contains(&point) {
                    sum + number
                } else {
                    sum
                }
            });
    }
}

fn create_win_conditions() -> Vec<Vec<Point>> {
    let mut win_conditions = Vec::new();
    let mut diag_win_one = Vec::new();
    let mut diag_win_two = Vec::new();
    for i in 0..5 {
        let mut row_win = Vec::new();
        let mut col_win = Vec::new();
        for j in 0..5 {
            row_win.push(Point(i, j));
            col_win.push(Point(j, i));
            if i == j {
                diag_win_one.push(Point(i, i));
                diag_win_two.push(Point(i, 5 - i));
            }
        }
        win_conditions.push(row_win);
        win_conditions.push(col_win);
    }
    win_conditions.push(diag_win_one);
    win_conditions.push(diag_win_two);
    return win_conditions;
}

fn parse_input(input: &str) -> (Vec<i32>, Vec<Board>) {
    let items: Vec<&str> = input.split("\n\n").collect();

    let first = items.first().expect("Missing draws");
    let draws = first
        .split(",")
        .map(|x| x.parse::<i32>().expect("Invalid number"))
        .collect();

    let rest = &items[1..];

    // Why doesn't this work?
    // let boards: Vec<Board> = rest.iter().map(parse_board).collect();
    let boards: Vec<Board> = rest
        .iter()
        .map(|board_str| parse_board(board_str))
        .collect();

    return (draws, boards);
}

fn parse_board(board_str: &str) -> Board {
    let mut board = Board::new();
    for (i, line) in board_str.lines().enumerate() {
        let places = line
            .as_bytes()
            .chunks(3) // Assuming BMP :shrug:
            .map(|chunk| {
                std::str::from_utf8(chunk)
                    .map(|it| it.trim().parse::<i32>().expect("Invalid number"))
                    .expect("Invalid number")
            });
        for (j, number) in places.enumerate() {
            let point = Point(j, i);
            board.points_by_number.insert(number, point);
        }
    }
    return board;
}

fn winning_score(input: &str) -> i32 {
    let (draws, boards) = parse_input(input);
    let (winning_board, draw) = winning_board(draws, boards).expect("No winning board!");
    return winning_board.sum_unmarked() * draw;
}

fn last_winning_score(input: &str) -> i32 {
    let (draws, boards) = parse_input(input);
    let (winning_board, draw) = last_winning_board(draws, boards).expect("No winning board!");
    return winning_board.sum_unmarked() * draw;
}

fn winning_board(draws: Vec<i32>, mut boards: Vec<Board>) -> Option<(Board, i32)> {
    let win_conditions = create_win_conditions();
    for draw in draws {
        for (i, board) in boards.iter_mut().enumerate() {
            if let Some(&point) = board.points_by_number.get(&draw) {
                board.marks.insert(point);
            }
            if board.is_win(&win_conditions) {
                // There's defintely a better way to do this!
                let winner = boards.remove(i);
                return Some((winner, draw));
            }
        }
    }
    return None;
}

fn last_winning_board(draws: Vec<i32>, mut boards: Vec<Board>) -> Option<(Board, i32)> {
    let win_conditions = create_win_conditions();
    for draw in draws {
        for board in &mut boards {
            if let Some(&point) = board.points_by_number.get(&draw) {
                board.marks.insert(point);
            }
        }
        let mut i = 0;
        while i < boards.len() {
            let board = boards.get(i).unwrap();
            if board.is_win(&win_conditions) {
                let board = boards.remove(i);
                if boards.len() == 0 {
                    return Some((board, draw));
                }
            } else {
                i += 1;
            }
        }
    }
    return None;
}

pub fn solve() {
    let input = include_str!("day4.txt");
    println!("day 4A: {}", winning_score(input));
    println!("day 4B: {}", last_winning_score(input));
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "7,4,9,5,11,17,23,2,0,14,21,24,10,16,13,6,15,25,12,22,18,20,8,19,3,26,1

22 13 17 11  0
 8  2 23  4 24
21  9 14 16  7
 6 10  3 18  5
 1 12 20 15 19

 3 15  0  2 22
 9 18 13 17  5
19  8  7 25 23
20 11 10 24  4
14 21 16 12  6

14 21 17 24  4
10 16 15  9 19
18  8 23 26 20
22 11 13  6  5
 2  0 12  3  7";

    #[test]
    fn test_example() {
        assert_eq!(4512, winning_score(INPUT));
    }

    #[test]
    fn test_example_part_two() {
        assert_eq!(1924, last_winning_score(INPUT));
    }
}
