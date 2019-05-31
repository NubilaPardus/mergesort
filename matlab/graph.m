%  
% @author NubilaPardus
%
n = [20
    200
    2000
    20000
    100000
    200000
    5000000
    10000000
    20000000
    30000000
    50000000
    ];

sequential_memory = [1018,1553,1234,1888,4239,6018,17386,39113,63469,77691,136231];
    
sequential_time = [3083669
    1379190
    4447178
    39436756
    61596433
    118839409
    188219772
    305726668
    1212504029
    1272995870
    2153865818
];

fork_join_memory = [1293,1697,1890,2578,4210,7764,15454,30554,47579,98534,150934];

fork_join_time = [26377476
    8585844
    12324465
    14607314
    15644442
    22683347
    49074316
    113353643
    332502002
    298797895
    561626337
];

executer_memory = [1320,1703,1946,2605,3891,6725,17948,30808,53315,106974,158181];
    
executer_time = [31449339
    10460625
    49368242
    52381894
    29909327
    47330453
    85499973
    198058996
    319609742
    437116274
    727697007
];

%------ Plotting
figure(1)
grid on
hold on
plot(n, sequential_time, 'c-o')
plot(n, fork_join_time, 'b-*')
plot(n, executer_time, 'm-p')
legend({'Sequential version', 'Parallel version (using a fork/join pool)', 'Parallel version (using executor service)'},'Location','northwest')
title('Schematic performance of Merge Sort Algorithm')
xlabel('Integer Array Size (n)')
ylabel('Total Time (nanosec)')


figure(2)
grid on
hold on
plot(n, sequential_memory, 'c-o')
plot(n, fork_join_memory, 'b-*')
plot(n, executer_memory, 'm-p')
legend({'Sequential version', 'Parallel version (using a fork/join pool)', 'Parallel version (using executor service)'},'Location','northwest')
title('Schematic performance of Merge Sort Algorithm')
xlabel('Integer Array Size (n)')
ylabel('Memory Usage (kb)')


