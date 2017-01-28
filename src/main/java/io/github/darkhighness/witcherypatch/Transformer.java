package io.github.darkhighness.witcherypatch;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class Transformer implements IClassTransformer
{
	static boolean isEnvObfuscated;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("com.emoniph.witchery.util.CreatureUtil"))
		{
			ClassNode classNode = readClassFromBytes(bytes);
			for (MethodNode node : classNode.methods)
			{
				if (node.name.equals("isInSunlight") | node.name.equals("checkForVampireDeath"))
				{
					node.instructions.insert(new InsnNode(Opcodes.IRETURN));
					node.instructions.insert(new InsnNode(Opcodes.ICONST_0));
				}
			}

			System.out.println("Got it");
			return writeClassToBytes(classNode);
		}
		return bytes;
	}

	// Shout out to squeek.
	private ClassNode readClassFromBytes(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClassToBytes(ClassNode classNode)
	{
		return writeClassToBytes(classNode, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
	}

	private byte[] writeClassToBytes(ClassNode classNode, int flags)
	{
		ClassWriter writer = new ClassWriter(flags);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				return method;
			}
		}
		return null;
	}

	private AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck)
	{
		return getOrFindInstruction(firstInsnToCheck, false);
	}

	private AbstractInsnNode getOrFindInstruction(AbstractInsnNode firstInsnToCheck, boolean reverseDirection)
	{
		for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = reverseDirection ? instruction.getPrevious() : instruction.getNext())
		{
			if (instruction.getType() != AbstractInsnNode.LABEL && instruction.getType() != AbstractInsnNode.LINE)
			{
				return instruction;
			}
		}
		return null;
	}

	private AbstractInsnNode findFirstInstruction(MethodNode method)
	{
		return getOrFindInstruction(method.instructions.getFirst());
	}
}
