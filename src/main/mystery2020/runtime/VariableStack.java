package mystery2020.runtime;

import java.util.Stack;

import AST.Decl;
import AST.VarDecl;

/**
 * A layered stack of locals.
 * 
 * Locals at level 0 are the globals.
 *
 * @author creichen
 *
 */
public class VariableStack {
	private Stack<ActivationRecord> stack;

	private VariableStack(Stack<ActivationRecord> new_stack) {
		this.stack = new_stack;
	}
	
	public Variable
	getVariable(int depth, int index) {
		if (depth >= this.stack.size()) {
			throw new RuntimeException("Invalid depth " + depth + ": max is " + this.stack.size());
		}
		return this.stack.get(depth).getVariables().get(index);
	}
	
	public void
	pop() {
		this.stack.pop();
	}
	
	public void
	push(ActivationRecord vv) {
		this.stack.push(vv);
	}
	
	public static VariableStack
	createEmpty() {
		return new VariableStack(new Stack<>());
	}
	
	@Override
	public String
	toString() {
		StringBuffer sb = new StringBuffer("VariableStack:\n");
		for (int i = 0; i < this.stack.size(); i++) {
			sb.append(i + ": " + this.stack.get(i).toString() + "\n");
		}
		return sb.toString();
	}

	/**
	 * Creates a shallow copy of this variable stack
	 *
	 * @return
	 */
	public VariableStack
	copyTruncated(int depth) {
		int size = this.stack.size();
		if (depth > size) {
			throw new RuntimeException("Can't truncate stack of size " + size + " to " + depth);
		}
		Stack<ActivationRecord> va = new Stack<>();
		for (int i = 0; i < depth; i++) {
			va.push(this.stack.get(i));
		}
		VariableStack result = new VariableStack(va);
		return result;
	}
	
	/**
	 * Creates a shallow copy of this variable stack
	 *
	 * @return
	 */
	public VariableStack
	copy() {
		Stack<ActivationRecord> va = new Stack<>();
		va.addAll(this.stack);
		return new VariableStack(va);
	}
	
	public Decl
	findDeclaration(String name) {
		for (int s_i = 0; s_i < this.stack.size(); ++s_i) {
			ActivationRecord ar = this.stack.get(s_i);
			for (Decl decl: ar.getDecls()) {
				if (((AST.Named)decl).name().equals(name)) {
					return decl;
				}
			}
		}
		return null;
	}

	public Variable getVariable(VarDecl vardecl) {
		for (int s_i = 0; s_i < this.stack.size(); ++s_i) {
			ActivationRecord ar = this.stack.get(s_i);
			for (Decl decl: ar.getDecls()) {
				if (decl == vardecl) {
					return ar.get(vardecl.accessIndex());
				}
			}
		}
		return null;
	}
}
