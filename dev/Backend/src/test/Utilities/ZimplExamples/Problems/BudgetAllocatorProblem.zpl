set MONTHS := {1..12};

param TOTAL_INCOME := 10000;
param MINIMUM_SAVINGS := 1000;

set LIMITS := {
    <"Housing", 1500, 2000>,
    <"Food", 400, 800>,
    <"Transport", 200, 500>,
    <"Utilities", 300, 500>
};

set CATEGORIES := proj(LIMITS, <1>);

var spending[CATEGORIES * MONTHS] real >= 0;
var savings[MONTHS] real >= 0;

# Monthly income constraint
subto monthly_budget:
    forall <m> in MONTHS:
        sum<c> in CATEGORIES:
            spending[c,m] + savings[m] <= TOTAL_INCOME;

# Category spending constraints using elements from LIMITS
subto category_limits:
    forall <c> in CATEGORIES:
        forall <m> in MONTHS:
            forall <cat, minval, maxval> in LIMITS with cat == c:
                spending[c,m] <= maxval;

# Basic minimum spending
subto minimum_spending:
    forall <c> in CATEGORIES:
        forall <m> in MONTHS:
            forall <cat, minval, maxval> in LIMITS with cat == c:
                spending[c,m] >= minval;

# Ensure minimum monthly savings
subto minimum_savings:
    forall <m> in MONTHS:
        savings[m] >= MINIMUM_SAVINGS;

# Optimization function with quadratic penalties
minimize budget_allocation:
    sum <m> in MONTHS: (
        # Quadratic penalties for spending deviations from middle
        sum <c> in CATEGORIES: (
            sum <cat, minval, maxval> in LIMITS with cat == c: (
                (spending[c,m] - (maxval + minval)/2)^2
            )
        ) +
        # Quadratic penalty for savings deviation from middle
        (savings[m] - (TOTAL_INCOME + MINIMUM_SAVINGS)/2)^2
    );