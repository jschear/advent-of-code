open! Core

let set_of_string string = Set.of_list (module Char) (String.to_list string)

let rucksacks =
  In_channel.read_lines "input/day3.txt" ~fix_win_eol:false
  |> List.map ~f:(fun line ->
         let half = String.length line / 2 in
         ( set_of_string (Str.string_before line half),
           set_of_string (Str.string_after line half) ))

let priority char =
  let ascii = Char.to_int char in
  if ascii >= 97 && ascii <= 122 then ascii - 96 else ascii - 38

let duplicates =
  List.map rucksacks ~f:(fun (first, second) ->
      let intersection = Set.inter first second in
      priority (List.nth_exn (Set.to_list intersection) 0))

let total_priority = List.fold duplicates ~init:0 ~f:( + )
let () = print_endline (Int.to_string total_priority)
