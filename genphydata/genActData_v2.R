require(lubridate)
require(rjson)

genSleepData <- function (startTime) {
     if (hour(startTime) < 20) {
        total_duration = abs(rnorm (1, 20 * 60, 10 * 60))
     } else {
        total_duration = abs(rnorm (1, 8 * 60 * 60, 60 * 60))
     }

     slp_data = list()
     time = startTime
     count = 1
     while (time < (startTime + total_duration)) {
         data = list()
         #睡眠狀態 = 0 : 進入睡眠, 1 : 淺層睡眠, 2 : 深度睡眠, 3 : 狀態切換或起床, 4 : 結束睡眠
         status = which(rmultinom(1, 1, prob = c(0.04, 0.1, 0.85, 0.1, 0.01))==1)
         duration = round(abs(switch(status, 
                       rnorm (1, 10 * 60, 2 * 60),
                       rnorm (1, 10 * 60, 5 * 60),
                       rnorm (1, 60 * 60, 10 * 60),
                       rnorm (1, 5 * 60, 1 * 60),
                       rnorm (1, 60, 60))))

         data$timestamp = time
         data$status = status
         data$duration = duration
         data$hrm_data = getHeartRateData (5, status, time, data$duration)
         slp_data[[count]] = data 
         time = time + duration
         count = count + 1
         if (status == 5) break
     }

     return(list(duration = (time - startTime), data=slp_data))
}

getSleepDataV2 <- function (time, duration) {
     data = list()
     #睡眠狀態 = 0 : 進入睡眠, 1 : 淺層睡眠, 2 : 深度睡眠, 3 : 狀態切換或起床, 4 : 結束睡眠
     status = which(rmultinom(1, 1, prob = c(0.04, 0.1, 0.85, 0.1, 0.01))==1)
     data$status = status
     data$hrm_data = getHeartRateData (5, status, time, duration)

     return(data)
}

getHeartRateData <- function (context, status, time, duration, report.period = 10) {
     num = round (duration / report.period)

     if (num == 0) return(NULL)

     hrm_data = list()
     for (i in 1:num) {
         heartRate = round( switch (context,
                       abs(rnorm (1, 70, 10)),
                       switch (status,
                               abs(rnorm (1, 80, 5)),
                               abs(rnorm (1, 85, 5)),
                               abs(rnorm (1, 90, 5))),
                       switch (status,
                               abs(rnorm (1, 100, 5)),
                               abs(rnorm (1, 110, 5)),
                               abs(rnorm (1, 120, 5)),
                               abs(rnorm (1, 130, 5))),
                       abs(rnorm (1, 100, 10)),
                       switch (status,
                               abs(rnorm (1, 70, 10)),
                               abs(rnorm (1, 68, 7)),
                               abs(rnorm (1, 66, 5)),
                               abs(rnorm (1, 70, 10)),
                               abs(rnorm (1, 75, 10)))
                     ))
         data = list()
         data$timestamp = time + i * report.period
         data$hrm_report = heartRate
         if (i == 1)
            hr_peak_rate = heartRate

         hr_peak_rate = max(heartRate, hr_peak_rate)
         data$hr_peak_rate = hr_peak_rate

         hrm_data[[i]] = data
     }

     return(hrm_data)
}

#    速度(m/min) 強度(MET) 3000公尺的運動時間(min) 運動3000公尺的單位體重能量消耗(kcal/kg)
#走路   52      3.5     57.7    3.53
#走路   66      4.5     45.5    3.58
#走路   80      5.5     37.5    3.61
#跑步   133     7.5     22.6    2.96
#跑步   200     9       15.0    2.36
#跑步   233     12      12.9    2.70
#走路一秒兩步,兩秒三步
#3.13m/s、3.58m/s、4.02 m/s

#自然的走路步幅約為身高減100，換句話說，175公分的人，約為75公分
#聲稱理想的步頻應是每分鐘180步
#初學者開始可能有點迷茫，尤其可能曾道聽途說所謂的「神奇180」，聲稱理想的步頻應是每分鐘180步。但是，如果因為不斷追求達到該理想步頻而忽略步幅的重要性，便不是正確的跑步觀念。步幅也有所謂的「理想標準」，約是個人身高>的1.1倍，但這需要根據個別跑者的速度來計算，並不完全適用於每個人。

#步頻與步幅的吻合度

#步速（分/千米）        步幅與身高比例  步頻（步/分））         

#男     女      男      女      
#6.67   0.54    0.55    155.4   160.8    2.50(內差法推估)
#4.76   0.72    0.75    162.6   172.8    2.46(內差法推估)  
#3.70   0.87    0.86    174.0   188.4    2.74(內差法推估)
#3.03   1.00    0.96    184.8   207.0    3.36(內差法推估)


#原文網址：https://kknews.cc/zh-tw/sports/any89rn.html

getStepData <- function (context, time, height = 170, weight = 65, sex = 1, active.type = 1) {
      type = 0
      duration = 0
      step = list()
      if (context == 2) { # walk
         duration = abs(rnorm (1, 10 * 60, 10 * 60))
         type = which(rmultinom(1, 1, prob = c(0.3, 0.3 ,0.3))==1)
         step.meter = (height - 100) / 100
         cal.type = c(3.53, 3.58, 3.61) * 1 / 3 #(cal/(m*kg))
         speed.type = c(52, 66, 80) / 60
         step$type = context
         step$distance = speed.type[type] * duration
         step$cal = step$distance * cal.type[type] * weight
         step$count = step$distance / step.meter
      } else { # run
         if (active.type == 0) {
            duration = abs(rnorm (1, 5 * 60, 2 * 60))
         } else {
            duration = abs(rnorm (1, 20 * 60, 5 * 60))
         }

         type = which(rmultinom(1, 1, prob = c(0.25, 0.25 ,0.25, 0.25))==1)
         if (sex == 1) { # man
            step.height.ratio = c(0.55, 0.75, 0.86, 0.96)
            step.frequency = c(160.8, 172.8, 188.4, 207.0)
         } else if (sex == 2) { #woman
            step.height.ratio = c(0.54, 0.72, 0.87, 1.00)
            step.frequency = c(155.4, 162.6, 174.0, 184.8)
         }
         step.meter = height * step.height.ratio[type] / 100
         cal.type = c(2.50, 2.46, 2.74, 3.36) * 1 / 3 #(cal/(m*kg))
         step$type = context
         step$count = step.frequency[type] / 60 * duration
         step$distance = step$count * step.meter
         step$cal = step$distance * cal.type[type] * weight
      }
     
      step$hrm_data = getHeartRateData (context, type, time, duration) 
      return(list(duration = duration, data = step))
}

getStepDataV2 <- function (context, time, duration, height = 170, weight = 65, sex = 1) {
      type = 0
      step = list()
      if (context == 2) { # walk
         type = which(rmultinom(1, 1, prob = c(0.3, 0.3 ,0.3))==1)
         step.meter = (height - 100) / 100
         cal.type = c(3.53, 3.58, 3.61) * 1 / 3 #(cal/(m*kg))
         speed.type = c(52, 66, 80) / 60
         #step$type = context
         step$distance = speed.type[type] * duration
         step$count = step$distance / step.meter
         step$cal = step$distance * cal.type[type] * weight
      } else { # run
         type = which(rmultinom(1, 1, prob = c(0.25, 0.25 ,0.25, 0.25))==1)
         if (sex == 1) { # man
            step.height.ratio = c(0.55, 0.75, 0.86, 0.96)
            step.frequency = c(160.8, 172.8, 188.4, 207.0)
         } else if (sex == 2) { #woman
            step.height.ratio = c(0.54, 0.72, 0.87, 1.00)
            step.frequency = c(155.4, 162.6, 174.0, 184.8)
         }
         step.meter = height * step.height.ratio[type] / 100
         cal.type = c(2.50, 2.46, 2.74, 3.36) * 1 / 3 #(cal/(m*kg))
         #step$type = context
         step$count = step.frequency[type] / 60 * duration
         step$distance = step$count * step.meter
         step$cal = step$distance * cal.type[type] * weight
      }

      step$hrm_data = getHeartRateData (context, type, time, duration)
      return(step)
}

getHourType <- function(t) {
    if (is.na(t)) return(NA)

    hour.time = hour(t)

    type = 1
    if ((hour.time >= 7) && (hour.time < 8))
       type = 1
    else if ((hour.time >= 8) && (hour.time < 12))
       type = 2
    else if ((hour.time >= 12) && (hour.time < 13))
       type = 3
    else if ((hour.time >= 13) && (hour.time < 16))
       type = 4
    else if ((hour.time >= 16) && (hour.time < 18))
       type = 5
    else if ((hour.time >= 18) && (hour.time < 21))
       type = 6
    else if ((hour.time < 7) || (hour.time >= 21))
       type = 7

    return (type)
}

getStartTimeforHourType <- function(t) {
    day = date(t)
    hour.time = hour(t)

    startTime = 7
    if ((hour.time >= 7) && (hour.time < 8))
       startTime = 7
    else if ((hour.time >= 8) && (hour.time < 12))
       startTime = 8
    else if ((hour.time >= 12) && (hour.time < 13))
       startTime = 12
    else if ((hour.time >= 13) && (hour.time < 16))
       startTime = 13
    else if ((hour.time >= 16) && (hour.time < 18))
       startTime = 16
    else if ((hour.time >= 18) && (hour.time < 21))
       startTime = 18 
    else if ((hour.time < 7) || (hour.time >= 21))
       startTime = 21

    if (hour.time < 7)
       day = day - 1
    
    startTime = as.POSIXlt(paste(day, startTime), format="%Y-%m-%d %H")
    return (startTime)
}

genContextTimeStamp <- function (startTime, duration, active.type = 1) {
     Sys.setlocale("LC_TIME", "en_US.UTF8")
     workdays = c("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

     print(startTime)
     t = startTime
     times = rep(startTime, 6)
     timestamp = NULL
     context = NULL
     while (t < (startTime + duration)) {
         weekday = weekdays(as.POSIXct(t, origin="1970-01-01"))
         hour.time = hour(t)
         type = getHourType (t)

         set.seed(runif(1)*100000000)
         if (weekday %in% workdays ) {
             if (active.type == 0) { #non active
                durations = switch(type,                    # static, walking, running, cycling, sleeping, others(tbd)
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(1*60))), mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60)))),    #  7 ~ 8
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), #  8 ~ 12
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(4*60)))),     # 12 ~ 13
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), # 13 ~ 16
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(4*60)))),    # 16 ~ 18
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(6*60))), mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), # 18 ~ 21
                             c(mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(9*60*60))), mean(rexp(100, 1/(10*60*60))), mean(rexp(100, 1/(20*60*60))), mean(rexp(100, 1/60)), mean(rexp(100, 1/(8*60*60)))) # 21 ~ 7
                           )
             } else {
                #print(type)
                durations = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(30*60)))),    # 7 ~ 8
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(10*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), # 8 ~ 12
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(4*60)))),     # 12 ~ 13
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(10*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), # 13 ~ 16   
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(4*60))), mean(rexp(100, 1/(5*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(30*60)))),    # 16 ~ 18
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(6*60))), mean(rexp(100, 1/(6*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(40*60)))), # 18 ~ 21
                             c(mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(9*60*60))), mean(rexp(100, 1/(10*60*60))), mean(rexp(100, 1/(20*60*60))), mean(rexp(100, 1/60)), mean(rexp(100, 1/(8*60*60))))  # 21 ~ 7
                           )
             }
         } else { #weekend
             if (active.type == 0) { #non active
                durations = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                             c(mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(40*60)))),    # 7 ~ 8
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(30*60)))),  # 8 ~ 12
                             c(mean(rexp(100, 1/(5*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60)))),    # 12 ~ 13
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(30*60)))),  # 13 ~ 16
                             c(mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(40*60)))),    # 16 ~ 18
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(30*60)))),  # 18 ~ 21
                             c(mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(10*60*60))), mean(rexp(100, 1/60)), mean(rexp(100, 1/(8*60*60))))  # 21 ~ 7
                           )
             } else {
                durations = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                             c(mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(30*60)))),    # 7 ~ 8
                             c(mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(15*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(50*60)))),  # 8 ~ 12
                             c(mean(rexp(100, 1/(10*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60)))),   # 12 ~ 13
                             c(mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(15*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(50*60)))),  # 13 ~ 16
                             c(mean(rexp(100, 1/(40*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(20*60))), mean(rexp(100, 1/(2*60*60))), mean(rexp(100, 1/(30*60)))),    # 16 ~ 18
                             c(mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(15*60))), mean(rexp(100, 1/(30*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(4*60*60))), mean(rexp(100, 1/(50*60)))),  # 18 ~ 21
                             c(mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(8*60*60))), mean(rexp(100, 1/(20*60*60))), mean(rexp(100, 1/60)), mean(rexp(100, 1/(8*60*60))))  # 21 ~ 7
                       )
             }
         }

         types = unlist(lapply(times + durations, getHourType))
         dates = unlist(lapply(times + durations, date))
         #cur.date = date(t)
         #print(types)
         #durations[which(types != type)] = NA
         times = times + durations
         t = min(times[!is.na(times)])
         cur.date = date(t)
         times[which(types != type)] = NA
         times[which(dates != cur.date)] = NA
         timestamp = c(timestamp, times)
         #print(times)
         if (getHourType(t) != type || is.na(t)) {
            t = getStartTimeforHourType(t)
            times = rep(t, 6)
         }
   }

   x= timestamp#[which(timestamp <= (startTime + duration))]
   x[is.na(x)] = max(x[!is.na(x)]) + 1
   y = sort(x, method = "quick", index.return = TRUE)
   idx = which(y$x <= (startTime + duration))
   y$x = y$x[idx]
   y$ix = y$ix[idx]
   y$hour = hour(as.POSIXct(y$x, origin="1970-01-01"))
   y$context = y$ix%%6

   return(y)
} 

#genBoundCtxData <- function (data, win = 60*60) {
#   ts = data$x
#   ct = data$context
#   s = seq(min(ts), max(ts), win)
#   for (is in s[-1]) {
#       idx = length(which(sign(ts - is)==-1))
#       ct = c(ct, ct[idx])
#   }
#   ts = c(ts, s[-1])
   
#   y = sort(ts, method = "quick", index.return = TRUE)
#   ct = ct[y$ix]
   
#   return (list (timestamp = y$x, hour = hour(as.POSIXct(y$x, origin = "1970-01-01")), context = ct, timeBounds = c(s[-1])))
#}

getBoundCtxData <- function (data, start, duration) {
   if (duration <= 0) return(NULL)

   end = start + duration
   if (end < min(data$x)) return(NULL)

   ts = data$x
   ct = data$context

   idx = which(ts >= start & ts <= end)
   ts.bound = ts[idx]
   ct.bound = ct[idx]
   
   start.idx = which(sign(ts - start) == 0)
   if (length(start.idx) == 0) {
      start.idx = length(which(sign(ts - start)==-1))
      if (start.idx != 0) {
         ct.bound = c(ct[start.idx], ct.bound)
         ts.bound = c(start, ts.bound)
      }
   }

   end.idx = which(sign(ts - end) == 0)
   if (length(end.idx) == 0) {
      end.idx = length(which(sign(ts - end)==-1))
      if (end.idx != 0) {
         ct.bound = c(ct.bound, ct[end.idx])
         ts.bound = c(ts.bound, end)
      }
   }

   return (list (timestamp = ts.bound[-length(ts.bound)], context = ct.bound[-length(ct.bound)], durations = diff(ts.bound)))
}

genBoundData <- function (uid, data, start, duration) {
   b.data <- getBoundCtxData (data, start, duration)

   ts = b.data$timestamp
   ct = b.data$context
   durations = b.data$durations

   r.data = list()
   for (i in 1:length(ts)) {
       r = list()
       result = switch(ct[i],
                       getHeartRateData (ct[i], 0, ts[i], durations[i]),    # static
                       getStepDataV2(ct[i], ts[i], durations[i]),         # walking
                       getStepDataV2(ct[i], ts[i], durations[i]),         # running
                       getHeartRateData (ct[i], 0, ts[i], durations[i]),
                       getSleepDataV2(ts[i], durations[i]))

       r$context = ct[i]
       r$timestamp = ts[i]
       r$duration = durations[i]

       if (ct[i] == 2 || ct[i] == 3) {
          r$count = result$count
          r$distance = result$distance
          r$cal = result$cal
          r$hrm = result$hrm_data       
       } else if (ct[i] == 5) {
          r$status = result$status
          r$hrm = result$hrm_data
       } else {
          r$hrm = result
       }
       r.data[[i]] = r
   }

   return (list(uuid = uid, data=r.data)) 
}

## Version 4 UUIDs have the form:
##    xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
##    where x is any hexadecimal digit and
##    y is one of 8, 9, A, or B
##    f47ac10b-58cc-4372-a567-0e02b2c3d479
uuid <- function(uppercase=FALSE) {

  hex_digits <- c(as.character(0:9), letters[1:6])
  hex_digits <- if (uppercase) toupper(hex_digits) else hex_digits

  y_digits <- hex_digits[9:12]

  paste(
    paste0(sample(hex_digits, 8), collapse=''),
    paste0(sample(hex_digits, 4), collapse=''),
    paste0('4', sample(hex_digits, 3), collapse=''),
    paste0(sample(y_digits,1),
           sample(hex_digits, 3),
           collapse=''),
    paste0(sample(hex_digits, 12), collapse=''),
    sep='-')
}

genActDataV2 <- function (startTime, simu_duration, win = 60 * 60, active.type = 1) {
   data <- genContextTimeStamp(startTime, simu_duration)

   s = seq(min(data$x), max(data$x), win)
   s = c(s, min(data$x) + length(s) * win)

   uid = uuid()
   durations = diff(s)
   rr <- list()

   for (i in 1 : (length(s)-1)) {
       rr[[i]] <- genBoundData(uid, data, s[i], durations[i])
   }

   return(rr)
}

genActData <- function (startTime, simu_duration, active.type = 1) {
     #weekdays(as.Date('16-08-2012','%d-%m-%Y'))
     Sys.setlocale("LC_TIME", "en_US.UTF8")
     workdays = c("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

     time = startTime

     sdata = list()
     count = 1
     
     while (time < (startTime + simu_duration)) {
         weekday = weekdays(as.POSIXct(time, origin="1970-01-01"))

         if (weekday %in% workdays ) {
             hour.time = hour(time)
             type = 1
             if ((hour.time > 7 && hour.time < 8) || (hour.time > 16 && hour.time < 18))
                type = 2 
             else if (hour.time < 7 || hour.time > 21)
                type = 3
             else if (hour.time > 12 && hour.time < 13)
                type = 4

             if (active.type == 0) { #non active
                context = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                           which(rmultinom(1, 1, prob = c(0.6, 0.15, 0.15, 0.05, 0.05)) == 1),
                           which(rmultinom(1, 1, prob = c(0.3, 0.4, 0.3, 0.199, 0.001)) == 1),
                           which(rmultinom(1, 1, prob = c(0.29, 0.005, 0.004, 0.001, 0.7)) == 1),
                           which(rmultinom(1, 1, prob = c(0.299, 0.2, 0.2, 0.001, 0.3)) == 1)
                       )
             } else {
                context = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                           which(rmultinom(1, 1, prob = c(0.4, 0.15, 0.15, 0.05, 0.05)) == 1),
                           which(rmultinom(1, 1, prob = c(0.1, 0.5, 0.4, 0.199, 0.001)) == 1),
                           which(rmultinom(1, 1, prob = c(0.19, 0.005, 0.004, 0.001, 0.8)) == 1),
                           which(rmultinom(1, 1, prob = c(0.199, 0.25, 0.25, 0.001, 0.3)) == 1)
                       )
             }
         } else { #weekend
             hour.time = hour(time)
             type = 1
             if ((hour.time > 7 && hour.time < 9) || (hour.time > 16 && hour.time < 18))
                type = 2
             else if (hour.time < 7 || hour.time > 21)
                type = 3

             if (active.type == 0) { #non active
                context = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                           which(rmultinom(1, 1, prob = c(0.2, 0.3, 0.2, 0.2, 0.1)) == 1),
                           which(rmultinom(1, 1, prob = c(0.1, 0.2, 0.3, 0.2, 0.2)) == 1),
                           which(rmultinom(1, 1, prob = c(0.29, 0.005, 0.004, 0.001, 0.7)) == 1)
                          )
             } else {
                context = switch(type,                      # static, walking, running, cycling, sleeping, others(tbd)
                           which(rmultinom(1, 1, prob = c(0.1, 0.2, 0.3, 0.3, 0.1)) == 1),
                           which(rmultinom(1, 1, prob = c(0.1, 0.1, 0.4, 0.3, 0.1)) == 1),
                           which(rmultinom(1, 1, prob = c(0.29, 0.005, 0.004, 0.001, 0.7)) == 1)
                       )
             }
         }

         duration = switch(context, 
                       abs(rnorm (1, 10 * 60, 5 * 60)),    # static
                       getStepData(context, time, active.type = active.type),         # walking
                       getStepData(context, time, active.type = active.type),         # running
                       if (active.type == 0) abs(rnorm (1, 60 * 60, 30 * 60))
                       else abs(rnorm (1, 10 * 60, 30 * 60)),   # cycling
                       genSleepData(time))                 # sleeping

         data = list()
         if (context == 2 || context == 3 || context == 5) {
            data$timestamp = time
            data$context = context
            data$data = duration$data
            data$duration = round(duration$duration)
         } else {
            data$timestamp = time
            data$context = context
            data$duration = abs(round(duration))
            data$hrm_data = getHeartRateData (context, 0, time, data$duration)
         }

         
         sdata[[count]] = data
         count = count + 1
         cat(context, data$duration, time, hour(time), "\n")
         time = time + data$duration
     }

     return(sdata)
}
