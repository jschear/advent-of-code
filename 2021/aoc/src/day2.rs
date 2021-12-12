#[derive(Copy, Clone)]
enum Strategy {
    Naive,
    Correct,
}

enum Action {
    Forward(i32),
    Down(i32),
    Up(i32),
}

struct Position {
    horizontal: i32,
    depth: i32,
    aim: i32,
}

impl Position {
    fn apply(&mut self, action: &Action, strategy: Strategy) {
        match (strategy, action) {
            (Strategy::Naive, Action::Forward(x)) => self.horizontal += x,
            (Strategy::Correct, Action::Forward(x)) => {
                self.horizontal += x;
                self.depth += self.aim * x;
            }
            (Strategy::Naive, Action::Down(x)) => self.depth += x,
            (Strategy::Correct, Action::Down(x)) => self.aim += x,
            (Strategy::Naive, Action::Up(x)) => self.depth -= x,
            (Strategy::Correct, Action::Up(x)) => self.aim -= x,
        }
    }
}

fn parse_input(input: &str) -> Vec<Action> {
    input
        .split("\n")
        .map(|line| {
            let mut split = line.split(" ");
            let action_name = split.next().unwrap();
            let magnitude: i32 = split.next().unwrap().parse().unwrap();
            match action_name {
                "forward" => Action::Forward(magnitude),
                "down" => Action::Down(magnitude),
                "up" => Action::Up(magnitude),
                _ => panic!("Unrecognized action"),
            }
        })
        .collect()
}

fn execute_plan(plan: &[Action], stategy: Strategy) -> i32 {
    let position = Position {
        horizontal: 0,
        depth: 0,
        aim: 0,
    };
    let final_position = plan.iter().fold(position, |mut position, action| {
        position.apply(action, stategy);
        position
    });
    return final_position.horizontal * final_position.depth;
}

pub fn solve() {
    let input = include_str!("day2.txt");
    let plan = parse_input(input);
    println!("day 2A: {}", execute_plan(&plan, Strategy::Naive));
    println!("day 2B: {}", execute_plan(&plan, Strategy::Correct));
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "forward 5
down 5
forward 8
up 3
down 8
forward 2";

    #[test]
    fn test_example() {
        let plan = parse_input(INPUT);
        assert_eq!(150, execute_plan(&plan, Strategy::Naive));
    }

    #[test]
    fn test_example_part_two() {
        let plan = parse_input(INPUT);
        assert_eq!(900, execute_plan(&plan, Strategy::Correct));
    }
}
