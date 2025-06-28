# Sets defining workers and shift types
set WORKERS := { "Alice", "Bob", "Charlie", "David", "Eve",
                "Denis", "Nadav", "Shlomo", "Adam", "Ron",
                "Dan", "Shelly", "Issac", "Ben", "Batya"};

# SHIFT_TYPES: <ShiftName, FromWeekday, ToWeekday, StartHour, EndHour, WorkersRequired>
set SHIFT_TYPES := {
    <"workday morning", 1, 5, 7, 15, 3>,
    <"workday afternoon", 1, 5, 15, 23, 2>,
     <"workday night", 1, 5, 15, 23, 1>,
     <"Friday morning", 6, 6, 7, 13, 1>,
     <"Friday noon", 6, 6, 13, 17, 1>,
     <"Friday evening", 6, 6, 17, 23, 1>,
     <"Friday night", 6, 6, 0, 7, 1>,
     <"Saturday morning", 7, 7, 7, 14, 1>,
     <"Saturday noon", 7, 7, 14, 19, 1>,
     <"Saturday end", 7, 7, 19, 24, 1>,
    <"weekend_day", 6, 7, 8, 20, 4>,
    <"peak_hours", 1, 5, 10, 14, 1>
};

# Basic parameters
param MIN_SHIFTS_PER_WORKER := 2;
param MAX_SHIFTS_PER_WEEK := 5;
param MAX_CONSECUTIVE_DAYS := 3;
param MIN_WEEKEND_WORKERS := 2;
param MAX_HOURS_PER_WEEK := 40;
param MIN_HOURS_BETWEEN_SHIFTS := 8;

# Set definitions
set WORKER_CONDITIONS := {};
set DAYS := {1 to 7};
set SHIFT_NAMES := proj(SHIFT_TYPES, <1>);
set VALID_SHIFT_DAYS := { <sn, d> in SHIFT_NAMES * DAYS |
    card({<s, df, dt, hf, ht, wr> in SHIFT_TYPES with
        s == sn and d >= df and d <= dt}) >= 1
};
set VALID_ASSIGNMENTS := WORKERS * VALID_SHIFT_DAYS;

# Parameters for shift properties
param shift_duration[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := ht - hf;
param shift_start[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := hf;
param shift_end[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := ht;
param workers_required[<n,df,dt,hf,ht,wr> in SHIFT_TYPES] := wr;

# Variables
# assign[worker, shift, weekday]
var assign[VALID_ASSIGNMENTS] binary;
var hours_worked[WORKERS] real >= 0;
var weekend_shifts[WORKERS] real >= 0;
var max_hours real >= 0;
var min_hours real >= 0;

# Core constraints
subto shift_coverage:
    forall <sn, d> in VALID_SHIFT_DAYS:
        sum <w> in WORKERS:
            assign[w, sn, d] == sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn: wr;

subto no_overlap:
    forall <w> in WORKERS:
        forall <d> in DAYS:
            sum <sn> in SHIFT_NAMES with <w, sn, d> in VALID_ASSIGNMENTS:
                assign[w, sn, d] <= 1;

subto max_shifts_per_week_constraint:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            assign[w, sn, d] <= MAX_SHIFTS_PER_WEEK;

subto min_shifts_per_worker:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            assign[w, sn, d] >= MIN_SHIFTS_PER_WORKER;

subto max_hours_per_week:
    forall <w> in WORKERS:
        sum <sn, d> in VALID_SHIFT_DAYS:
            sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn:
                assign[w, sn, d] * shift_duration[name, df, dt, hf, ht, wr] <= MAX_HOURS_PER_WEEK;

subto max_consecutive_days:
    forall <w> in WORKERS:
        forall <d> in {1 to 5}:
            sum <k> in {0 to MAX_CONSECUTIVE_DAYS} with d + k <= 7:
                sum <sn> in SHIFT_NAMES with <w, sn, d + k> in VALID_ASSIGNMENTS:
                    assign[w, sn, d + k] <= MAX_CONSECUTIVE_DAYS;

subto min_hours_between_shifts:
    forall <w> in WORKERS:
        forall <sn1, d1> in VALID_SHIFT_DAYS:
            forall <sn2, d2> in VALID_SHIFT_DAYS with d2 == d1 + 1:
                forall <n1,df1,dt1,hf1,ht1,wr1> in SHIFT_TYPES with n1 == sn1:
                    forall <n2,df2,dt2,hf2,ht2,wr2> in SHIFT_TYPES with n2 == sn2:
                        if (hf2 + 24 - ht1 < MIN_HOURS_BETWEEN_SHIFTS) then
                            assign[w, sn1, d1] + assign[w, sn2, d2] <= 1
                        end;

#Worker can't be assigned during their unavailable times
subto respect_unavailability:
    forall <w, sn, d> in VALID_ASSIGNMENTS:
        forall <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn and d >= df and d <= dt:
            forall <worker, day_from, day_to, hour_from, hour_to> in WORKER_CONDITIONS with
                   worker == w and d >= day_from and d <= day_to:
                if hf < hour_to and ht > hour_from then
                    assign[w, sn, d] == 0
                end;

# Auxiliary variable constraints
subto calculate_hours_worked:
    forall <w> in WORKERS:
        hours_worked[w] ==
            sum <sn, d> in VALID_SHIFT_DAYS:
                sum <name, df, dt, hf, ht, wr> in SHIFT_TYPES with name == sn:
                    assign[w, sn, d] * shift_duration[name, df, dt, hf, ht, wr];

subto calculate_weekend_shifts:
    forall <w> in WORKERS:
        weekend_shifts[w] ==
            sum <sn, d> in VALID_SHIFT_DAYS with d >= 6:
                assign[w, sn, d];

subto set_max_hours:
    forall <w> in WORKERS:
        hours_worked[w] <= max_hours;

subto set_min_hours:
    forall <w> in WORKERS:
        hours_worked[w] >= min_hours;

# Objective function
minimize total_obj:
    sum <w> in WORKERS: hours_worked[w] +
    2 * sum <w> in WORKERS: weekend_shifts[w] +
    3 * (max_hours - min_hours);