open! Core

let rec list_max l =
  match l with [] -> Int.min_value | x :: xs -> max x (list_max xs)

let elves =
  In_channel.read_all "day1.txt"
  |> Str.split (Str.regexp "\n\n")
  |> List.map ~f:(Str.split (Str.regexp "\n"))
  |> List.map ~f:(List.map ~f:int_of_string)

let sums = List.map elves ~f:(List.fold ~init:0 ~f:( + ))

let () =
  let max = list_max sums in
  Printf.printf "Part 1: %d\n" max

let () =
  let sorted = List.sort sums ~compare |> List.rev in
  let sum_three_max =
    match sorted with a :: b :: c :: _ -> a + b + c | _ -> 0
  in
  Printf.printf "Part 2: %d\n" sum_three_max
