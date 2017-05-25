%struct.__sFILE = type { i8*, i32, i32, i16, i16, %struct.__sbuf, i32, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i64 }
%struct.__sFILEX = type opaque
%struct.__sbuf = type { i8*, i32 }

@__stderrp = external global %struct.__sFILE*, align 8
@.str = private unnamed_addr constant [29 x i8] c"ARGS: expected %d, found %d\0A\00", align 1

; Function Attrs: nounwind ssp uwtable
define i32 @main(i32, i8**) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i8**, align 8
  store i32 0, i32* %3, align 4
  store i32 %0, i32* %4, align 4
  store i8** %1, i8*** %5, align 8
  %6 = load i32, i32* %4, align 4
  %7 = icmp ne i32 3, %6
  br i1 %7, label %8, label %12

; <label>:8:                                      ; preds = %2
  %9 = load %struct.__sFILE*, %struct.__sFILE** @__stderrp, align 8
  %10 = load i32, i32* %4, align 4
  %11 = call i32 (%struct.__sFILE*, i8*, ...) @fprintf(%struct.__sFILE* %9, i8* getelementptr inbounds ([29 x i8], [29 x i8]* @.str, i32 0, i32 0), i32 3, i32 %10)
  store i32 1, i32* %3, align 4
  br label %13

; <label>:12:                                     ; preds = %2
  store i32 0, i32* %3, align 4
  br label %13

; <label>:13:                                     ; preds = %12, %8
  %14 = load i32, i32* %3, align 4
  ret i32 %14
}

declare i32 @fprintf(%struct.__sFILE*, i8*, ...) #1
