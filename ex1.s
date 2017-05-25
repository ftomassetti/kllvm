	.section	__TEXT,__text,regular,pure_instructions
	.macosx_version_min 10, 12
	.globl	_add
	.p2align	4, 0x90
_add:                                   ## @add
	.cfi_startproc
## BB#0:
                                        ## kill: %ESI<def> %ESI<kill> %RSI<def>
                                        ## kill: %EDI<def> %EDI<kill> %RDI<def>
	leal	(%rdi,%rsi), %eax
	retq
	.cfi_endproc

	.globl	_main
	.p2align	4, 0x90
_main:                                  ## @main
	.cfi_startproc
## BB#0:
	pushq	%rax
Lcfi0:
	.cfi_def_cfa_offset 16
	xorl	%edi, %edi
	movl	$97, %esi
	callq	_add
	movl	%eax, %edi
	callq	_putchar
	popq	%rax
	retq
	.cfi_endproc


.subsections_via_symbols
