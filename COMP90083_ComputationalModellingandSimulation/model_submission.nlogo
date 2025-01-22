; Define agents and variables
breed [sharks shark]
breed [fishers fisher]
globals
[
  year
  month
  life-expectancy-in-months
  reproduce-months
  initial-sharks
]

sharks-own
[
  is-female?
  age-in-months
  offspring-rate
  mature-age-in-months
  last-time-reproduce
]

fishers-own
[
  initial-y
  total-shark-harvested
  monthly-shark-harvested
  harvest-limit
  avg-ticks-with-rewards
  num-ticks-with-rewards
  cooperate?
]


;; Initialization
to setup

  ca
  if set-random-seed [random-seed fixed-seed]

  ; Set parameters and globals
  set life-expectancy-in-months 16 * 12
  set reproduce-months []

  ; Shade the patches
  ask patches
  [
    ifelse (remainder (pxcor + pycor) 2) = 0.0
    [ set pcolor 9]
    [ set pcolor 9.5]
  ]

  ; Create sharks
  ask patches
  [
    sprout-sharks initial-shark-per-patch
    [
      set is-female? false
      set color grey
      if random-float 1 < 0.5 [
        set is-female? true
        set offspring-rate 6 + random 8.5 - 4 ; mean-off-spring ± 4
      ]
      set shape "circle"
      set size 0.05
      setxy (pxcor - 0.4 + random-float 0.8) (pycor - 0.4 + random-float 0.8)
      set age-in-months (1 + random 15.1) * 12
      set mature-age-in-months (6 + random 4) * 12
      update-gender-color
      set last-time-reproduce (- random 24.5) - 6 ; -6 to -30
    ]
  ]

  ; Create harvestors (fishers)
  create-fishers fishers-num
  [
    move-to one-of patches
    set initial-y pycor
    set color scale-color green initial-y -3 3
    setxy (pxcor - 0.2 + random-float 0.4) (pycor - 0.2 + random-float 0.4)
    set size 0.4
    set shape "person"
    set total-shark-harvested 0
    set monthly-shark-harvested 0

    ifelse detection-comm and random 100 < cooperate-ratio
      [ set cooperate? true ]
      [ set cooperate? false ]
  ]

  ; reset time step, and store the initial shark population
  reset-ticks
  set initial-sharks count sharks

  update-LPI-graph
end


;; Reset the model parameters to default calibrated model under null theory
to null-calibrated
  set chance-captured 3.5
  set fishers-num 20
  set initial-shark-per-patch 100
  set max-monthly-harvest 100
  set force-local-harvesting false
  set min-harvest-len 0
  set detection-comm false
  set cooperate-ratio 0
  set set-random-seed false
  set fixed-seed 0
  set runtime-limit-year 50
  set growth-coef 0.075
  set asymp-size 325.4
  set age-when-size-0 3.342
  set patch-max-shark 300
  set coop-not-share-sharks 30
  set female-len-offset 10
end


;; GO procedure
to go
  ; check for simulation termination
  if should-stop
  [
    stop
  ]

  ; track simulation time and shark age
  update-year-and-age

  ; fishers stay or move to a new patch
  fisher-move

  ; fishers harvest sharks on their current patch
  fisher-harvest

  ; sharks reproduce
  reproduce

  ; update time step
  tick

  ; update visualisation graphs
  update-reproduction-graph
  update-LPI-graph
  update-harvest-graph
end


; Fisher related processes
;; Each fisher stay or move to a new patch
to fisher-move
  ask fishers [
    ; find a destination patch and move there if successful
    let next-patch find-next-patch
    if next-patch != nobody
    [
      move-to next-patch
      ; slight adjustment in location within the patch for better visualisation (so fishers dont overlap)
      setxy (pxcor - 0.2 + random-float 0.4) (pycor - 0.2 + random-float 0.4)
    ]

  ]

end

;; Fisher operation: find a destination patch according to strategy
to-report find-next-patch
  ; find patches with available shark
  let available-patches patches with [ (count sharks-here) > 0 ]

  ; for LH strategy, restrict to patches within the same row as the fisher
  if force-local-harvesting
    [ set available-patches patches with [ (count sharks-here) > 0 and pycor = [initial-y] of myself ]]

  ; return any identified patch, or the one with maximum sharks for COMM strategy
  ifelse detection-comm
    [ report max-one-of available-patches [count sharks-here] ]
    [ report one-of available-patches ]
end

;; Each fisher harvest fishes on their patch
to fisher-harvest
  ; reset the monthly shark harvest to 0
  ask fishers [set monthly-shark-harvested 0]

  ; determine the harvest-limit
  ifelse detection-comm
    [ harvest-communcation ]
    [ ask fishers [set harvest-limit max-monthly-harvest ] ]

  ; harvest shark
  sharks-be-harvested

  ; update measurement for sustainability
  ask fishers
  [
    if monthly-shark-harvested > 0
    [
      set num-ticks-with-rewards num-ticks-with-rewards + 1
      set avg-ticks-with-rewards (rolling-avg avg-ticks-with-rewards ticks num-ticks-with-rewards)
    ]
  ]
end

;; Each shark checks if they are harvested by a fisher
to sharks-be-harvested
  ask sharks
  [
    let is-captured False
    let shark-length length-from-age-gender

    ; sharks are only harvestable if they exceed min-harvest-len
    if shark-length > min-harvest-len
    [
      ; check if captured by a fisher on the patch who has not reached their harvest-limit
      ask fishers-here with [monthly-shark-harvested < harvest-limit]
      [
        if not is-captured and random-float 100 < chance-captured
        [
          set is-captured True
          set total-shark-harvested total-shark-harvested + 1
          set monthly-shark-harvested monthly-shark-harvested + 1
        ]
      ]
      if is-captured [die]
    ]
  ]
end

;; Each fisher determines their harvest-limit (called only under COMM strategy)
to harvest-communcation
  ask fishers
  [
    ; for cooperative fishers, their harvest limit is determined by available sharks and number of fishers on the current patch.
    ifelse cooperate?
      [ set harvest-limit (count sharks-here - coop-not-share-sharks)/ (count fishers-here) ]
      [ set harvest-limit 10000 ] ; very large limit
  ]
end


; Patch related processes
;; Patch's procedure: check if the maximum shark capacity has been reached
to-report reached-max-sharks?
  report count sharks-here >= patch-max-shark
end


; Shark related processes
;; Track time and shark age
to update-year-and-age
  ; update time
  set month month + 1
  if month > 12
  [
    set month 1
    set year year + 1
  ]

  ; update shark age and remove if reached life expectancy
  ask sharks
  [
    set age-in-months age-in-months + 1
    update-gender-color
    if age-in-months > life-expectancy-in-months [die]
  ]
end

;; Shark's procedure: update shark color and size based on age and gender
to update-gender-color
  if is-mature?
  [
    ifelse is-female? [set color pink] [set color blue]
    set size 0.1
  ]
end

;; Shark's procedure: check if the shark is mature
to-report is-mature?
  report age-in-months > mature-age-in-months
end


;; Shark's procedure: estimate length from gender, and from age using von Bertalanffy growth function (VBGF)
to-report length-from-age-gender
  let age-in-yrs age-in-months / 12
  let offset 0
  if is-female? [ set offset female-len-offset ]
  report (asymp-size * ( 1 - exp ( - growth-coef  * ( age-in-yrs + age-when-size-0 ) ) ) + offset)

end


;; Each shark reproduce
to reproduce
  ; calculate heat level (chance of reproduction)
  ; 1 --> 10 --> 12 months old
  ; 0.2 --> 1 --> 0.1 heat level
  let months-from-peak abs (month - 10)
  let heat-level (10 - months-from-peak) / 11.11 + 0.1

  ; sharks on patches that have not yet reached maximum capacity can reproduce
  ask patches
  [
    if not reached-max-sharks?
    [
      ; check for presence of mature male sharks, and find female mature sharks
      let male-mature-shark sharks-here with [not is-female? and is-mature?]
      let female-mature-shark sharks-here with [is-female? and is-mature?]
      if any? male-mature-shark
      [
        ; female sharks reproduce
        ask female-mature-shark with [ticks >= last-time-reproduce + 2 * 12 and random-float 1 < heat-level]
        [
          set reproduce-months lput month reproduce-months
          set last-time-reproduce ticks
          ; initialize baby sharks
          hatch-sharks offspring-rate
          [
            set is-female? false
            set color grey
            if random-float 1 < 0.5 [
              set is-female? true
              set offspring-rate 6 + random 8.5 - 4 ; mean-off-spring ± 4
            ]
            set shape "circle"
            set size 0.05
            setxy (pxcor - 0.4 + random-float 0.8) (pycor - 0.4 + random-float 0.8)
            set age-in-months 0
            set mature-age-in-months (6 + random 4) * 12
            set last-time-reproduce -2 * 12
          ]

        ]
      ]
    ]

  ]
end


; Visualisations and Graphs
;; Update LPI graph: line graph tracking proportion of sharks remaining relative to initial shark population
to update-LPI-graph
  set-current-plot "LPI"
  set-plot-pen-mode 0
  let x-value ticks / 12
  let lpi count sharks / initial-sharks
  plotxy x-value lpi
end

;; Update return graph: line graph tracking the average number of shark harvested per agent at each month
to update-harvest-graph
  set-current-plot "Mean Monthly Sharks Harvest"
  set-plot-pen-mode 0
  let x-value ticks / 12
  let shark-harvest-count mean [monthly-shark-harvested] of fishers
  plotxy x-value shark-harvest-count
end

;; Update reproduction graph: histogram tracking the reproductions by month
to update-reproduction-graph
  set-current-plot "Reproduction Months"
  set-plot-pen-mode 1
  histogram reproduce-months
end



; Utils
;; Calculate the rolling average given a new sample value
to-report rolling-avg [prev-avg new-value new-num]
  report ((prev-avg * (new-num - 1)) + new-value) / new-num
end
;; Check if the simulation should stop based on time step and shark availability
to-report should-stop
  report ticks >= runtime-limit-year * 12 or count sharks = 0
end
;; Calculate sustainaibility
to-report sustainability
  report mean [avg-ticks-with-rewards] of fishers
end
@#$#@#$#@
GRAPHICS-WINDOW
391
14
907
241
-1
-1
72.7
1
10
1
1
1
0
1
0
1
-3
3
-1
1
0
0
1
tick
30.0

BUTTON
24
31
87
64
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
97
31
160
64
Step
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
891
357
980
402
total sharks
count sharks
17
1
11

BUTTON
166
31
229
64
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
1151
211
1337
244
max-monthly-harvest
max-monthly-harvest
0
100
100.0
1
1
NIL
HORIZONTAL

PLOT
18
280
218
430
LPI
year
%
0.0
1.0
0.0
1.0
true
false
"" ""
PENS
"pen-0" 1.0 0 -16777216 true "" ""
"pen-1" 1.0 0 -2674135 true "" "plotxy ticks / 12 1"

SLIDER
22
157
194
190
chance-captured
chance-captured
0
100
3.5
1
1
%
HORIZONTAL

MONITOR
675
302
808
347
Patches with no shark
count patches with [count sharks-here = 0]
17
1
11

MONITOR
813
302
940
347
Patches with shark
count patches with [any? sharks-here]
17
1
11

MONITOR
674
357
887
402
Average number of sharks per patch
mean [count sharks-here] of patches
4
1
11

TEXTBOX
929
211
1124
242
NUM: cap the maximum number of sharks to be harvested monthly.
11
0.0
1

SWITCH
1150
42
1347
75
force-local-harvesting
force-local-harvesting
1
1
-1000

TEXTBOX
928
44
1153
74
LH: fishers must stay in the same row throughout.
11
0.0
1

MONITOR
946
302
1085
347
Sustainability
sustainability
4
1
11

PLOT
459
278
659
428
Reproduction Months
month
#reproduction
1.0
13.0
0.0
10.0
true
false
"" ""
PENS
"pen-0" 1.0 0 -16777216 true "" ""

SLIDER
1151
164
1323
197
min-harvest-len
min-harvest-len
0
300
0.0
1
1
NIL
HORIZONTAL

TEXTBOX
929
161
1144
197
LEN: fishers can only capture sharks over a certain length
11
0.0
1

PLOT
240
278
440
428
Mean Monthly Sharks Harvest
year
#sharks
0.0
1.0
0.0
3.0
true
false
"" ""
PENS
"pen-0" 1.0 0 -16777216 true "" ""

SWITCH
1150
85
1309
118
detection-comm
detection-comm
1
1
-1000

SLIDER
1151
121
1312
154
cooperate-ratio
cooperate-ratio
0
100
0.0
1
1
%
HORIZONTAL

TEXTBOX
929
88
1145
119
COMM: Allow communication to maintain minimum shark population
11
0.0
1

MONITOR
1123
354
1363
399
Mean total harvested of cooperate fishers
mean [total-shark-harvested] of fishers with [harvest-limit != 10000]
17
1
11

MONITOR
1123
300
1362
345
Mean total harvested of defect fishers
mean [total-shark-harvested] of fishers with [harvest-limit = 10000]
17
1
11

SLIDER
204
156
379
189
initial-shark-per-patch
initial-shark-per-patch
0
150
100.0
10
1
NIL
HORIZONTAL

SLIDER
21
617
189
650
runtime-limit-year
runtime-limit-year
0
100
50.0
10
1
NIL
HORIZONTAL

SWITCH
22
516
188
549
set-random-seed
set-random-seed
1
1
-1000

TEXTBOX
23
488
181
516
Experiment Settings
11
15.0
1

INPUTBOX
21
552
188
612
fixed-seed
0.0
1
0
Number

SLIDER
22
199
194
232
fishers-num
fishers-num
19
21
20.0
1
1
NIL
HORIZONTAL

SLIDER
240
513
412
546
growth-coef
growth-coef
0.07125
0.07875
0.075
0.00375
1
NIL
HORIZONTAL

SLIDER
242
552
414
585
asymp-size
asymp-size
309.13
341.67
325.4
16.27
1
NIL
HORIZONTAL

SLIDER
242
590
414
623
age-when-size-0
age-when-size-0
3.1749
3.5091
3.342
0.1671
1
NIL
HORIZONTAL

BUTTON
23
76
146
109
null-calibrated
null-calibrated
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
205
200
377
233
patch-max-shark
patch-max-shark
255
345
300.0
45
1
NIL
HORIZONTAL

SLIDER
426
513
628
546
coop-not-share-sharks
coop-not-share-sharks
25.5
34.5
30.0
4.5
1
NIL
HORIZONTAL

SLIDER
242
628
414
661
female-len-offset
female-len-offset
8.5
11.5
10.0
1.5
1
NIL
HORIZONTAL

TEXTBOX
23
134
232
162
Model settings
11
15.0
1

TEXTBOX
928
22
1078
40
Strategies
11
15.0
1

TEXTBOX
155
80
306
110
Reset to default calibrated model under null-theory
11
0.0
1

TEXTBOX
243
485
477
513
Additional Sensitivity Analysis Parameters
11
15.0
1

@#$#@#$#@
## WHAT IS IT?

This section could give a general understanding of what the model is trying to show or explain.

## HOW IT WORKS

This section could explain what rules the agents use to create the overall behavior of the model.

## HOW TO USE IT

This section could explain how to use the model, including a description of each of the items in the interface tab.

## THINGS TO NOTICE

This section could give some ideas of things for the user to notice while running the model.

## THINGS TO TRY

This section could give some ideas of things for the user to try to do (move sliders, switches, etc.) with the model.

## EXTENDING THE MODEL

This section could give some ideas of things to add or change in the procedures tab to make the model more complicated, detailed, accurate, etc.

## NETLOGO FEATURES

This section could point out any especially interesting or unusual features of NetLogo that the model makes use of, particularly in the Procedures tab.  It might also point out places where workarounds were needed because of missing features.

## RELATED MODELS

This section could give the names of models in the NetLogo Models Library or elsewhere which are of related interest.

## CREDITS AND REFERENCES

This section could contain a reference to the model's URL on the web if it has one, as well as any other necessary credits or references.
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
0
Rectangle -7500403 true true 151 225 180 285
Rectangle -7500403 true true 47 225 75 285
Rectangle -7500403 true true 15 75 210 225
Circle -7500403 true true 135 75 150
Circle -16777216 true false 165 76 116

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.4.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="calibration-p1" repetitions="20" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>year</metric>
    <metric>month</metric>
    <metric>count sharks / initial-sharks</metric>
    <steppedValueSet variable="chance-captured" first="3" step="0.1" last="4"/>
    <steppedValueSet variable="initial-shark-per-patch" first="50" step="50" last="200"/>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="10"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="calibration-p2" repetitions="20" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>reproduce-months</metric>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="null" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="local-harvest" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
      <value value="false"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="comm" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="cooperate-ratio" first="0" step="10" last="100"/>
  </experiment>
  <experiment name="comm-heuristic" repetitions="20" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="cooperate-ratio" first="40" step="2" last="50"/>
  </experiment>
  <experiment name="harvest-len" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <steppedValueSet variable="min-harvest-len" first="60" step="10" last="170"/>
  </experiment>
  <experiment name="max-monthly-harvest" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <steppedValueSet variable="max-monthly-harvest" first="0" step="1" last="5"/>
  </experiment>
  <experiment name="max-monthly-harvest-1000sharkperpatch" repetitions="20" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <steppedValueSet variable="max-monthly-harvest" first="0" step="1" last="7"/>
  </experiment>
  <experiment name="sensitivity-chance-captured-harvest-len-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="min-harvest-len">
      <value value="170"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-chance-captured-max-monthly-harvest-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="max-monthly-harvest">
      <value value="3"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-max-monthly-harvest-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="max-monthly-harvest">
      <value value="3"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-harvest-len-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="min-harvest-len">
      <value value="170"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-length-harvest-len" repetitions="1" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <steppedValueSet variable="min-harvest-len" first="70" step="10" last="160"/>
    <enumeratedValueSet variable="len-factor">
      <value value="0.95"/>
      <value value="1"/>
      <value value="1.05"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-length-growth-coef-harvest-len" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <steppedValueSet variable="min-harvest-len" first="60" step="10" last="170"/>
    <enumeratedValueSet variable="growth-coef">
      <value value="0.06375"/>
      <value value="0.075"/>
      <value value="0.08625"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-length-asymp-size-harvest-len" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <steppedValueSet variable="min-harvest-len" first="60" step="10" last="170"/>
    <enumeratedValueSet variable="asymp-size">
      <value value="276.59"/>
      <value value="325.4"/>
      <value value="374.21"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-length-age-when-size-0-harvest-len" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <steppedValueSet variable="min-harvest-len" first="60" step="10" last="170"/>
    <enumeratedValueSet variable="age-when-size-0">
      <value value="2.8407"/>
      <value value="3.342"/>
      <value value="3.8433"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="certainty-len-harvest-len-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>len-uncertainty</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="1000"/>
    <enumeratedValueSet variable="len-uncertainty">
      <value value="false"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="min-harvest-len">
      <value value="160"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="uncertainty-len-harvest-len-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="1000"/>
    <enumeratedValueSet variable="len-uncertainty">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="min-harvest-len">
      <value value="160"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="lh+comm" repetitions="20" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-harvest-len-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="min-harvest-len">
      <value value="170"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-max-monthly-harvest-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="max-monthly-harvest">
      <value value="3"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-chance-captured-null" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-null" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-null" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-chance-captured-LH" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-LH" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-LH" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-chance-captured-COMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-COMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-COMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-coop-not-share-sharks-COMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="coop-not-share-sharks">
      <value value="25.5"/>
      <value value="30"/>
      <value value="34.5"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-chance-captured-LHCOMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="chance-captured">
      <value value="2.975"/>
      <value value="3.5"/>
      <value value="4.025"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-fishers-sharks-LHCOMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="fishers-num">
      <value value="17"/>
      <value value="20"/>
      <value value="23"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="initial-shark-per-patch">
      <value value="85"/>
      <value value="100"/>
      <value value="115"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-patch-max-shark-LHCOMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="patch-max-shark">
      <value value="255"/>
      <value value="300"/>
      <value value="345"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-coop-not-share-sharks-LHCOMM-best" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <enumeratedValueSet variable="force-local-harvesting">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="detection-comm">
      <value value="true"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cooperate-ratio">
      <value value="100"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="coop-not-share-sharks">
      <value value="25.5"/>
      <value value="30"/>
      <value value="34.5"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="sensitivity-length-female-len-offset-harvest-len" repetitions="1" runMetricsEveryStep="false">
    <preExperiment>null-calibrated</preExperiment>
    <setup>setup</setup>
    <go>go</go>
    <metric>month</metric>
    <metric>year</metric>
    <metric>sustainability</metric>
    <metric>count sharks</metric>
    <metric>count patches with [count sharks-here &gt; 0]</metric>
    <metric>should-stop</metric>
    <metric>force-local-harvesting</metric>
    <metric>detection-comm</metric>
    <metric>cooperate-ratio</metric>
    <metric>mean [monthly-shark-harvested] of fishers</metric>
    <runMetricsCondition>month = 1 or should-stop</runMetricsCondition>
    <enumeratedValueSet variable="runtime-limit-year">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="set-random-seed">
      <value value="true"/>
    </enumeratedValueSet>
    <steppedValueSet variable="fixed-seed" first="1" step="1" last="30"/>
    <steppedValueSet variable="min-harvest-len" first="60" step="10" last="170"/>
    <enumeratedValueSet variable="female-len-offset">
      <value value="8.5"/>
      <value value="10"/>
      <value value="11.5"/>
    </enumeratedValueSet>
  </experiment>
</experiments>
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
