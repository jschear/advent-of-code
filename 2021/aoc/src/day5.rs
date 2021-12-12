use std::cmp::Ordering;
use std::collections::HashMap;
use std::num::ParseIntError;
use std::str::FromStr;

#[derive(Hash, Eq, PartialEq, Debug, Clone, Copy)]
struct Point {
    x: i32,
    y: i32,
}

impl FromStr for Point {
    type Err = ParseIntError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let coords: Vec<&str> = s.split(',').collect();
        let x_fromstr = coords[0].parse::<i32>()?;
        let y_fromstr = coords[1].parse::<i32>()?;
        Ok(Point {
            x: x_fromstr,
            y: y_fromstr,
        })
    }
}

#[derive(Debug)]
struct Line {
    start: Point,
    end: Point,
}

impl Line {
    fn points(&self, include_diagonals: bool) -> Vec<Point> {
        if !include_diagonals && (self.start.x != self.end.x && self.start.y != self.end.y) {
            return vec![];
        }
        let xs = match self.start.x.cmp(&self.end.x) {
            Ordering::Less => (self.start.x..=self.end.x).collect(),
            Ordering::Greater => (self.end.x..=self.start.x).rev().collect(),
            Ordering::Equal => {
                let count = (self.end.y - self.start.y).abs() + 1;
                vec![self.start.x; count.try_into().unwrap()]
            }
        };
        let ys = match self.start.y.cmp(&self.end.y) {
            Ordering::Less => (self.start.y..=self.end.y).collect(),
            Ordering::Greater => (self.end.y..=self.start.y).rev().collect(),
            Ordering::Equal => {
                let count = (self.end.x - self.start.x).abs() + 1;
                vec![self.start.y; count.try_into().unwrap()]
            }
        };
        xs.iter()
            .zip(ys.iter())
            .map(|(&x, &y)| Point { x: x, y: y })
            .collect()
    }
}

fn parse_input(input: &str) -> Vec<Line> {
    input
        .lines()
        .map(|line: &str| line.split_once(" -> ").unwrap())
        .map(|(first, second)| Line {
            start: first.parse().unwrap(),
            end: second.parse().unwrap(),
        })
        .collect()
}

fn count_dangerous_areas(input: &str, include_diagonals: bool) -> usize {
    let lines = parse_input(input);
    let map: HashMap<Point, i32> = lines.iter().fold(HashMap::new(), |mut acc, line| {
        for point in line.points(include_diagonals) {
            let counter = acc.entry(point).or_insert(0);
            *counter += 1;
        }
        acc
    });
    map.values().filter(|&x| *x >= 2).count()
}

pub fn solve() {
    let input = include_str!("day5.txt");
    println!("day 5A: {}", count_dangerous_areas(input, false));
    println!("day 5B: {}", count_dangerous_areas(input, true));
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "0,9 -> 5,9
8,0 -> 0,8
9,4 -> 3,4
2,2 -> 2,1
7,0 -> 7,4
6,4 -> 2,0
0,9 -> 2,9
3,4 -> 1,4
0,0 -> 8,8
5,5 -> 8,2";

    #[test]
    fn test_example() {
        assert_eq!(5, count_dangerous_areas(INPUT, false));
    }

    #[test]
    fn test_example_part_two() {
        assert_eq!(12, count_dangerous_areas(INPUT, true));
    }
}
