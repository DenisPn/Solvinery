param MONTHLY_INCOME := 1500;
param MINIMUM_SAVINGS := 100;
param DESIRED_SAVINGS := 200;

set EXTRA_EARNINGS_SET := {
    <"Tax return", 800>,
    <"Holiday bonus", 400>,
    <"Side gig payment", 200>
};

set EXTRA_EXPENSES_SET := {
    <"Holiday gifts", 300>,
    <"Car insurance deductible", 400>
};

# Calculate monthly averages from the sets
param EXTRA_EARNINGS :=
    (sum<reason, amount> in EXTRA_EARNINGS_SET: amount);

param EXTRA_EXPENSES :=
    (sum<reason, amount> in EXTRA_EXPENSES_SET: amount);


# Category definition with priority weights
set Bills := {
    # <category, min, max, priority_weight>
    <"Rent", 1400, 1400, 10>,
    <"Utilities", 120, 250, 8>,
    <"Groceries", 300, 450, 9>,
    <"Transport", 150, 150, 10>,
    <"Entertainment", 50, 200, 3>
};

set CATEGORIES := proj(Bills, <1>);

var spending[CATEGORIES] integer >= 0;
var savings integer >= 0;
var savings_deficit real >= 0;

# Monthly budget must be fully allocated
subto monthly_budget:
    sum<c> in CATEGORIES:
        spending[c] + savings == MONTHLY_INCOME + EXTRA_EARNINGS;

# Category spending constraints
subto category_Bills:
    forall <c> in CATEGORIES:
        forall <cat, minval, maxval, weight> in Bills with cat == c:
            spending[c] <= maxval;

# Basic minimum spending
subto minimum_spending:
    forall <c> in CATEGORIES:
        forall <cat, minval, maxval, weight> in Bills with cat == c:
            spending[c] >= minval;

# For zero-weight categories, force minimum spending
subto zero_weight_categories:
    forall <c> in CATEGORIES:
        forall <cat, minval, maxval, weight> in Bills with cat == c and weight == 0:
            spending[c] == minval;

# Minimum savings with extra expenses
subto minimum_savings:
    savings >= MINIMUM_SAVINGS;

# Calculate savings deficit relative to desired amount
subto savings_deficit_calc:
    savings_deficit >= DESIRED_SAVINGS - savings;

minimize budget_allocation:
    (
        sum <c> in CATEGORIES: (
            sum <cat, minval, maxval, weight> in Bills with cat == c and minval != maxval and weight > 0: (
                (weight/10) * (spending[c] - maxval)^2
            )
        )
    ) + (savings_deficit)^2;