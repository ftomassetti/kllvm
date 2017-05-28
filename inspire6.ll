; ModuleID = 'src/test/resources/inspire6.c'
source_filename = "src/test/resources/inspire6.c"
target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-apple-macosx10.12.0"

%struct.__sFILE = type { i8*, i32, i32, i16, i16, %struct.__sbuf, i32, i8*, i32 (i8*)*, i32 (i8*, i8*, i32)*, i64 (i8*, i64, i32)*, i32 (i8*, i8*, i32)*, %struct.__sbuf, %struct.__sFILEX*, i32, [3 x i8], [1 x i8], %struct.__sbuf, i32, i64 }
%struct.__sFILEX = type opaque
%struct.__sbuf = type { i8*, i32 }

@__stderrp = external global %struct.__sFILE*, align 8
@.str = private unnamed_addr constant [8 x i8] c"%s: %s\0A\00", align 1
@.str.1 = private unnamed_addr constant [13 x i8] c"empty string\00", align 1
@.str.2 = private unnamed_addr constant [13 x i8] c"not a number\00", align 1
@.str.3 = private unnamed_addr constant [8 x i8] c"input a\00", align 1
@.str.4 = private unnamed_addr constant [13 x i8] c"input a: %d\0A\00", align 1
@.str.5 = private unnamed_addr constant [8 x i8] c"input b\00", align 1
@.str.6 = private unnamed_addr constant [13 x i8] c"input b: %f\0A\00", align 1
@.str.7 = private unnamed_addr constant [13 x i8] c"input c: %s\0A\00", align 1

; Function Attrs: nounwind ssp uwtable
define void @error(i8*, i8*) #0 {
  %3 = alloca i8*, align 8
  %4 = alloca i8*, align 8
  store i8* %0, i8** %3, align 8
  store i8* %1, i8** %4, align 8
  %5 = load %struct.__sFILE*, %struct.__sFILE** @__stderrp, align 8
  %6 = load i8*, i8** %3, align 8
  %7 = load i8*, i8** %4, align 8
  %8 = call i32 (%struct.__sFILE*, i8*, ...) @fprintf(%struct.__sFILE* %5, i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str, i32 0, i32 0), i8* %6, i8* %7)
  call void @exit(i32 1) #3
  unreachable
                                                  ; No predecessors!
  ret void
}

declare i32 @fprintf(%struct.__sFILE*, i8*, ...) #1

; Function Attrs: noreturn
declare void @exit(i32) #2

; Function Attrs: nounwind ssp uwtable
define i32 @parseInt(i8*, i8*) #0 {
  %3 = alloca i8*, align 8
  %4 = alloca i8*, align 8
  %5 = alloca i32, align 4
  %6 = alloca i32, align 4
  %7 = alloca i8, align 1
  store i8* %0, i8** %3, align 8
  store i8* %1, i8** %4, align 8
  %8 = load i8*, i8** %4, align 8
  %9 = call i64 @strlen(i8* %8)
  %10 = icmp eq i64 %9, 0
  br i1 %10, label %11, label %13

; <label>:11:                                     ; preds = %2
  %12 = load i8*, i8** %3, align 8
  call void @error(i8* %12, i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.1, i32 0, i32 0))
  br label %13

; <label>:13:                                     ; preds = %11, %2
  store i32 0, i32* %5, align 4
  store i32 0, i32* %6, align 4
  br label %14

; <label>:14:                                     ; preds = %42, %13
  %15 = load i32, i32* %6, align 4
  %16 = sext i32 %15 to i64
  %17 = load i8*, i8** %4, align 8
  %18 = call i64 @strlen(i8* %17)
  %19 = icmp ult i64 %16, %18
  br i1 %19, label %20, label %45

; <label>:20:                                     ; preds = %14
  %21 = load i32, i32* %6, align 4
  %22 = sext i32 %21 to i64
  %23 = load i8*, i8** %4, align 8
  %24 = getelementptr inbounds i8, i8* %23, i64 %22
  %25 = load i8, i8* %24, align 1
  store i8 %25, i8* %7, align 1
  %26 = load i8, i8* %7, align 1
  %27 = sext i8 %26 to i32
  %28 = icmp slt i32 %27, 48
  br i1 %28, label %33, label %29

; <label>:29:                                     ; preds = %20
  %30 = load i8, i8* %7, align 1
  %31 = sext i8 %30 to i32
  %32 = icmp sgt i32 %31, 57
  br i1 %32, label %33, label %35

; <label>:33:                                     ; preds = %29, %20
  %34 = load i8*, i8** %3, align 8
  call void @error(i8* %34, i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.2, i32 0, i32 0))
  br label %35

; <label>:35:                                     ; preds = %33, %29
  %36 = load i32, i32* %5, align 4
  %37 = mul nsw i32 %36, 10
  %38 = load i8, i8* %7, align 1
  %39 = sext i8 %38 to i32
  %40 = sub nsw i32 %39, 48
  %41 = add nsw i32 %37, %40
  store i32 %41, i32* %5, align 4
  br label %42

; <label>:42:                                     ; preds = %35
  %43 = load i32, i32* %6, align 4
  %44 = add nsw i32 %43, 1
  store i32 %44, i32* %6, align 4
  br label %14

; <label>:45:                                     ; preds = %14
  %46 = load i32, i32* %5, align 4
  ret i32 %46
}

declare i64 @strlen(i8*) #1

; Function Attrs: nounwind ssp uwtable
define float @parseFloat(i8*, i8*) #0 {
  %3 = alloca i8*, align 8
  %4 = alloca i8*, align 8
  %5 = alloca float, align 4
  %6 = alloca i32, align 4
  %7 = alloca i32, align 4
  %8 = alloca i32, align 4
  %9 = alloca i8, align 1
  store i8* %0, i8** %3, align 8
  store i8* %1, i8** %4, align 8
  %10 = load i8*, i8** %4, align 8
  %11 = call i64 @strlen(i8* %10)
  %12 = icmp eq i64 %11, 0
  br i1 %12, label %13, label %15

; <label>:13:                                     ; preds = %2
  %14 = load i8*, i8** %3, align 8
  call void @error(i8* %14, i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.1, i32 0, i32 0))
  br label %15

; <label>:15:                                     ; preds = %13, %2
  store float 0.000000e+00, float* %5, align 4
  store i32 0, i32* %6, align 4
  store i32 1, i32* %7, align 4
  store i32 0, i32* %8, align 4
  br label %16

; <label>:16:                                     ; preds = %60, %15
  %17 = load i32, i32* %8, align 4
  %18 = sext i32 %17 to i64
  %19 = load i8*, i8** %4, align 8
  %20 = call i64 @strlen(i8* %19)
  %21 = icmp ult i64 %18, %20
  br i1 %21, label %22, label %63

; <label>:22:                                     ; preds = %16
  %23 = load i32, i32* %8, align 4
  %24 = sext i32 %23 to i64
  %25 = load i8*, i8** %4, align 8
  %26 = getelementptr inbounds i8, i8* %25, i64 %24
  %27 = load i8, i8* %26, align 1
  store i8 %27, i8* %9, align 1
  %28 = load i32, i32* %6, align 4
  %29 = icmp eq i32 %28, 0
  br i1 %29, label %30, label %35

; <label>:30:                                     ; preds = %22
  %31 = load i8, i8* %9, align 1
  %32 = sext i8 %31 to i32
  %33 = icmp eq i32 %32, 46
  br i1 %33, label %34, label %35

; <label>:34:                                     ; preds = %30
  store i32 1, i32* %6, align 4
  br label %59

; <label>:35:                                     ; preds = %30, %22
  %36 = load i8, i8* %9, align 1
  %37 = sext i8 %36 to i32
  %38 = icmp slt i32 %37, 48
  br i1 %38, label %43, label %39

; <label>:39:                                     ; preds = %35
  %40 = load i8, i8* %9, align 1
  %41 = sext i8 %40 to i32
  %42 = icmp sgt i32 %41, 57
  br i1 %42, label %43, label %45

; <label>:43:                                     ; preds = %39, %35
  %44 = load i8*, i8** %3, align 8
  call void @error(i8* %44, i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.2, i32 0, i32 0))
  br label %45

; <label>:45:                                     ; preds = %43, %39
  %46 = load float, float* %5, align 4
  %47 = fmul float %46, 1.000000e+01
  %48 = load i8, i8* %9, align 1
  %49 = sext i8 %48 to i32
  %50 = sub nsw i32 %49, 48
  %51 = sitofp i32 %50 to float
  %52 = fadd float %47, %51
  store float %52, float* %5, align 4
  %53 = load i32, i32* %6, align 4
  %54 = icmp eq i32 %53, 1
  br i1 %54, label %55, label %58

; <label>:55:                                     ; preds = %45
  %56 = load i32, i32* %7, align 4
  %57 = mul nsw i32 %56, 10
  store i32 %57, i32* %7, align 4
  br label %58

; <label>:58:                                     ; preds = %55, %45
  br label %59

; <label>:59:                                     ; preds = %58, %34
  br label %60

; <label>:60:                                     ; preds = %59
  %61 = load i32, i32* %8, align 4
  %62 = add nsw i32 %61, 1
  store i32 %62, i32* %8, align 4
  br label %16

; <label>:63:                                     ; preds = %16
  %64 = load float, float* %5, align 4
  %65 = load i32, i32* %7, align 4
  %66 = sitofp i32 %65 to float
  %67 = fdiv float %64, %66
  ret float %67
}

; Function Attrs: nounwind ssp uwtable
define i32 @main(i32, i8**) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  %5 = alloca i8**, align 8
  %6 = alloca i32, align 4
  %7 = alloca float, align 4
  %8 = alloca i8*, align 8
  store i32 0, i32* %3, align 4
  store i32 %0, i32* %4, align 4
  store i8** %1, i8*** %5, align 8
  %9 = load i8**, i8*** %5, align 8
  %10 = getelementptr inbounds i8*, i8** %9, i64 1
  %11 = load i8*, i8** %10, align 8
  %12 = call i32 @parseInt(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.3, i32 0, i32 0), i8* %11)
  store i32 %12, i32* %6, align 4
  %13 = load i32, i32* %6, align 4
  %14 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.4, i32 0, i32 0), i32 %13)
  %15 = load i8**, i8*** %5, align 8
  %16 = getelementptr inbounds i8*, i8** %15, i64 2
  %17 = load i8*, i8** %16, align 8
  %18 = call float @parseFloat(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.5, i32 0, i32 0), i8* %17)
  store float %18, float* %7, align 4
  %19 = load float, float* %7, align 4
  %20 = fpext float %19 to double
  %21 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.6, i32 0, i32 0), double %20)
  %22 = load i8**, i8*** %5, align 8
  %23 = getelementptr inbounds i8*, i8** %22, i64 3
  %24 = load i8*, i8** %23, align 8
  store i8* %24, i8** %8, align 8
  %25 = load i8*, i8** %8, align 8
  %26 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([13 x i8], [13 x i8]* @.str.7, i32 0, i32 0), i8* %25)
  ret i32 0
}

declare i32 @printf(i8*, ...) #1

attributes #0 = { nounwind ssp uwtable "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-jump-tables"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #1 = { "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #2 = { noreturn "correctly-rounded-divide-sqrt-fp-math"="false" "disable-tail-calls"="false" "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "no-signed-zeros-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="penryn" "target-features"="+cx16,+fxsr,+mmx,+sse,+sse2,+sse3,+sse4.1,+ssse3,+x87" "unsafe-fp-math"="false" "use-soft-float"="false" }
attributes #3 = { noreturn }

!llvm.module.flags = !{!0}
!llvm.ident = !{!1}

!0 = !{i32 1, !"PIC Level", i32 2}
!1 = !{!"Apple LLVM version 8.1.0 (clang-802.0.42)"}
