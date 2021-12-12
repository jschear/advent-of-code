fn min_alignment_fuel<F>(input: &str, cost_fn: F) -> i32
where
    F: Fn(i32, i32) -> i32,
{
    let positions = input
        .split(",")
        .map(|s| s.parse().unwrap())
        .collect::<Vec<i32>>();

    let max = positions.iter().max().unwrap();
    let mut fuel_costs = vec![0; *max as usize];

    for i in 0..fuel_costs.len() {
        fuel_costs[i] = positions
            .iter()
            .map(|&position| cost_fn(position, i as i32))
            .sum();
    }
    *fuel_costs.iter().min().unwrap()
}

pub fn solve() {
    let input = include_str!("day7.txt");
    println!("day 7A: {}", min_alignment_fuel(input, distance_cost));
    println!(
        "day 7B: {}",
        min_alignment_fuel(input, arithmetic_series_cost)
    );
}

fn distance_cost(pos: i32, final_pos: i32) -> i32 {
    (pos - final_pos).abs()
}

fn arithmetic_series_cost(pos: i32, final_pos: i32) -> i32 {
    let distance = distance_cost(pos, final_pos);
    distance * (distance + 1) / 2
}

#[cfg(test)]
mod tests {
    use super::*;

    static INPUT: &str = "16,1,2,0,4,2,7,1,2,14";

    #[test]
    fn test_example() {
        assert_eq!(37, min_alignment_fuel(INPUT, distance_cost));
    }

    #[test]
    fn test_example_part_two() {
        assert_eq!(168, min_alignment_fuel(INPUT, arithmetic_series_cost))
    }
}
