package dev.ultreon.devices.virtual.components;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class Processor {
    public static final int INSN_NOP = 0x0000; // No operation
    public static final int INSN_PUSH = 0x0001; // Push value on stack
    public static final int INSN_POP = 0x0002; // Pop value from stack
    public static final int INSN_JMP = 0x0003; // Jump to address
    public static final int INSN_CALL = 0x0004; // Call subroutine at address
    public static final int INSN_RET = 0x0005; // Return from subroutine
    public static final int INSN_HALT = 0x0006; // Halt the processor
    public static final int INSN_ADD = 0x0007; // Add value to stack
    public static final int INSN_SUB = 0x0008; // Subtract value from stack
    public static final int INSN_MUL = 0x0009; // Multiply value on stack
    public static final int INSN_DIV = 0x000A; // Divide value on stack
    public static final int INSN_MOD = 0x000B; // Modulo value on stack
    public static final int INSN_AND = 0x000C; // Bitwise AND value on stack
    public static final int INSN_OR = 0x000D; // Bitwise OR value on stack
    public static final int INSN_XOR = 0x000E; // Bitwise XOR value on stack
    public static final int INSN_SHL = 0x000F; // Shift value on stack left
    public static final int INSN_SHR = 0x0010; // Shift value on stack right
    public static final int INSN_NOT = 0x0011; // Bitwise NOT value on stack
    public static final int INSN_FADD = 0x0012; // Floating point add
    public static final int INSN_FSUB = 0x0013; // Floating point subtract
    public static final int INSN_FMUL = 0x0014; // Floating point multiply
    public static final int INSN_FDIV = 0x0015; // Floating point divide
    public static final int INSN_FMOD = 0x0016; // Floating point modulo
    public static final int INSN_DADD = 0x0017; // 64-bit floating point add
    public static final int INSN_DSUB = 0x0018; // 64-bit floating point subtract
    public static final int INSN_DMUL = 0x0019; // 64-bit floating point multiply
    public static final int INSN_DDIV = 0x001A; // 64-bit floating point divide
    public static final int INSN_DMOD = 0x001B; // 64-bit floating point modulo
    public static final int INSN_LADD = 0x001C; // 64-bit integer point add
    public static final int INSN_LSUB = 0x001D; // 64-bit integer point subtract
    public static final int INSN_LMUL = 0x001E; // 64-bit integer point multiply
    public static final int INSN_LDIV = 0x001F; // 64-bit integer point divide
    public static final int INSN_LMOD = 0x0020; // 64-bit integer point modulo
    public static final int INSN_LAND = 0x0021; // 64-bit bitwise AND
    public static final int INSN_LOR = 0x0022; // 64-bit bitwise OR
    public static final int INSN_LXOR = 0x0023; // 64-bit bitwise XOR
    public static final int INSN_MOV = 0x0024; // Move value to stack
    public static final int INSN_CMP = 0x0025; // Compare value to stack
    public static final int INSN_JEQ = 0x0026; // Jump if value is zero
    public static final int INSN_JNE = 0x0027; // Jump if value is not zero
    public static final int INSN_JLT = 0x0028; // Jump if value is less than zero
    public static final int INSN_JGT = 0x0029; // Jump if value is greater than zero
    public static final int INSN_JLE = 0x002A; // Jump if value is less than or equal to zero
    public static final int INSN_JGE = 0x002B; // Jump if value is greater than or equal to zero
    public static final int INSN_JSR = 0x002C; // Jump to subroutine
    public static final int INSN_RETN = 0x002D; // Return from subroutine
    public static final int INSN_INT = 0x002E; // Interrupt
    public static final int INSN_IRET = 0x002F; // Interrupt return
    public static final int INSN_SYSCALL = 0x0030; // System call
    public static final int INSN_PUTMEM = 0x0031; // Put value in memory
    public static final int INSN_GETMEM = 0x0032; // Get value from memory
    public static final int INSN_PUTMEML = 0x0033; // Put value in memory 64-bit
    public static final int INSN_GETMEML = 0x0034; // Get value from memory 64-bit
    public static final int INSN_PUTREG = 0x0035; // Put value in register
    public static final int INSN_EUSR = 0x0036; // Enter user mode

    private static final int INT_ILLEGAL_INSTRUCTION = 0x0001;
    private static final int INT_DIVISION_BY_ZERO = 0x0002;
    private static final int INT_STACK_OVERFLOW = 0x0003;
    private static final int INT_STACK_UNDERFLOW = 0x0004;
    private static final int INT_INVALID_OPCODE = 0x0005;
    private static final int INT_INVALID_REGISTER = 0x0006;
    private static final int INT_INVALID_MEMORY_ACCESS = 0x0007;

    private int[] registers = new int[32];
    private long[] registers64 = new long[32];

    private final Memory memory;
    private int programCounter;

    public Processor(Memory memory) {
        this.memory = memory;
    }

    public int getStackPointer() {
        return memory.getInt(0x80004000);
    }

    public void setStackPointer(int value) {
        memory.setInt(0x80004000, value);
    }

    public void pushStack(int value) {
        setStackPointer(getStackPointer() - 4);
        memory.setInt(getStackPointer(), value);
    }

    public int popStack() {
        int value = memory.getInt(getStackPointer());
        setStackPointer(getStackPointer() + 4);
        return value;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void jump(int address) {
        programCounter = address;
    }

    public void step() {
        programCounter++;

        short insn = memory.getShort(programCounter);
        programCounter += Short.BYTES;
        switch (insn) {
            case INSN_NOP -> {
            }
            case INSN_PUSH -> {
                pushStack(memory.getInt(programCounter));
                programCounter += Integer.BYTES;
            }
            case INSN_POP -> memory.setInt(programCounter, popStack());
            case INSN_JMP -> jump(memory.getInt(programCounter + 2));
            case INSN_CALL -> callSubroutine(memory.getInt(programCounter + 2));
            case INSN_RET -> returnFromSubroutine();
            case INSN_PUTMEM -> {
                int register = memory.getByte(programCounter);
                if (register <= 32) {
                    int value = registers[register];
                    memory.setInt(memory.getInt(programCounter), value);
                    programCounter += Integer.BYTES;
                } else if (register == 65) {
                    int value = memory.getInt(memory.getInt(programCounter ));
                    programCounter += Integer.BYTES;
                    memory.setInt(memory.getInt(programCounter + 4), value);
                    programCounter += Integer.BYTES;
                }
            }
            case INSN_GETMEM -> {
                int register = memory.getByte(programCounter);
                if (register <= 32) {
                    int value = memory.getInt(memory.getInt(programCounter));
                    registers[register] = value;
                } else if (register == 65) {
                    int value = memory.getInt(memory.getInt(programCounter + 4));
                    memory.setInt(memory.getInt(programCounter), value);
                }
            }
            case INSN_PUTMEML -> {
                int register = memory.getByte(programCounter);
                if (register <= 32) {
                    long value = memory.getLong(memory.getInt(programCounter));
                    registers64[register - 32] = value;
                } else if (register == 65) {
                    long value = memory.getLong(memory.getInt(programCounter + 4));
                    memory.setLong(memory.getInt(programCounter), value);
                }
            }
            case INSN_GETMEML -> {
                int register = memory.getByte(programCounter);
                if (register <= 32) {
                    memory.setLong(memory.getInt(programCounter), registers64[register - 32]);
                } else if (register == 65) {
                    memory.setLong(memory.getInt(programCounter + 4), registers64[31]);
                }
            }
            case INSN_PUTREG -> {
                int register = memory.getByte(programCounter);
                if (register <= 32) {
                    int value = memory.getInt(programCounter + 4);
                    registers[register] = value;
                } else if (register == 65) {
                    long value = memory.getLong(programCounter + 4);
                    registers64[31] = value;
                }
            }
            case INSN_MOV -> {
                int register = memory.getByte(programCounter);
                int otherRegister = memory.getByte(programCounter);
                if (register <= 32 && otherRegister <= 32) {
                    int value = registers[otherRegister];
                    registers[register] = value;
                } else if (register <= 64 && otherRegister <= 64) {
                    long value = registers64[otherRegister - 32];
                    registers64[register - 32] = value;
                } else {
                    interrupt(INT_ILLEGAL_INSTRUCTION);
                }
            }
            case INSN_CMP -> {
                int register = memory.getByte(programCounter);
                int otherRegister = memory.getByte(programCounter);
                if (register <= 32 && otherRegister <= 32) {
                    int value = registers[otherRegister];
                    if (registers[register] != value) {
                        jump(memory.getInt(programCounter + 4));
                    }
                } else if (register <= 64 && otherRegister <= 64) {
                    long value = registers64[otherRegister - 32];
                    if (registers64[register - 32] != value) {
                        jump(memory.getInt(programCounter + 4));
                    }
                }
            }
        }
    }

    public int getInterruptAddress(int interrupt) {
        return memory.getInt(0x80005000 + interrupt * 4);
    }

    public void interrupt(int interrupt) {
        pushStack(programCounter);
        memory.setInt(0x80006000, programCounter);
        memory.setInt(0x80006010, interrupt);
        jump(getInterruptAddress(interrupt));
    }

    public void returnFromInterrupt() {
        jump(memory.getInt(0x80006000));
    }

    public void callSubroutine(int address) {
        pushStack(programCounter);
        jump(address);
    }

    public void enterUserMode(int address) {
        if (memory.getByte(0x80006020) != 0) {
            interrupt(INT_ILLEGAL_INSTRUCTION);
            return;
        }

        memory.setByte(0x80006020, (byte) 1);
    }

    public void syscall(int syscall) {
        if (memory.getByte(0x80006020) != 1) {
            interrupt(INT_ILLEGAL_INSTRUCTION);
            return;
        }

        pushStack(programCounter);
        jump(getMemory().getInt(0x80008000 + syscall * Integer.BYTES));
    }

    public void returnFromSubroutine() {
        jump(popStack());
    }

    public Memory getMemory() {
        return memory;
    }
}
