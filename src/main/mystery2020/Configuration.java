package mystery2020;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import mystery2020.runtime.ArrayAssignmentSemantics;
import mystery2020.runtime.ArrayEquality;
import mystery2020.runtime.ClosureEnvironmentBinding;
import mystery2020.runtime.LiteralType;
import mystery2020.runtime.OperandEvaluationOrder;
import mystery2020.runtime.ParameterEvaluationOrder;
import mystery2020.runtime.ParameterPassingMode;
import mystery2020.runtime.ProcedureSubtyping;
import mystery2020.runtime.ScopingMode;
import mystery2020.runtime.ShortCircuitEvaluation;
import mystery2020.runtime.TypeCheck;
import mystery2020.runtime.TypeNamesTYPE;
import mystery2020.runtime.UnknownType;
import mystery2020.runtime.VariableStorageBinding;

public class Configuration {
	// ====================
	// limits
	private int step_limit = 10000;
	private int call_limit = 500;
	public static final int ARRAY_SIZE_LIMIT = 65536;


	// ====================
	private static Function<String, String> id_stringifier = null;

	public static void
	setIDStringifier(Function<String, String> s) {
		Configuration.id_stringifier = s;
	}

	public static String
	stringifyID(String id) {
		if (id_stringifier == null) {
			return id;
		}
		return id_stringifier.apply(id);
	}

	// ====================
	// operators

	public enum Op {
		ADD("+"),
		EQ("=="),
		GT(">"),
		AND("AND");

		private String sourcename;
		private Op(String sourcename) {
			this.sourcename = sourcename;
		}
		public String
		getSourcename() {
			return this.sourcename;
		}
	}

	private Map<Op, OpConfig> op_config = new HashMap<>();
	{
		this.op_config.put(Op.ADD, new OpConfig(3, OpConfig.Associativity.RIGHT));
		this.op_config.put(Op.EQ,  new OpConfig(1, OpConfig.Associativity.NONE));
		this.op_config.put(Op.GT,  new OpConfig(2, OpConfig.Associativity.NONE));
		this.op_config.put(Op.AND, new OpConfig(0, OpConfig.Associativity.RIGHT));
	}

	public Map<Op, OpConfig>
	getOpConfig() {
		return this.op_config;
	}

	public static final String OP_SUBSYSTEM_CODE = "OP";

	// ====================
	// Configuration for all other subsystems
	// generic part
	private static final int OP_SUBSYSTEM_INDEX = -1; // special handling
	private static Map<String, Integer> subsystems_by_name = new HashMap<>();
	private static Map<ConfigSubsystem<?>, Integer> subsystems_index = new HashMap<>();
	private static List<ConfigSubsystem<?>> subsystems_ordered = new ArrayList<>();
	private ArrayList<ConfigSubsystem<?>.Config> configurations = new ArrayList<>();

	{
		subsystems_by_name.put(OP_SUBSYSTEM_CODE, OP_SUBSYSTEM_INDEX);
	}

	static int
	getConfigSubsystemIndex(ConfigSubsystem<?> subsystem) {
		if (Configuration.subsystems_index.containsKey(subsystem)) {
			return Configuration.subsystems_index.get(subsystem);
		}
		// we have never seen this subsystem before
		int new_index = subsystems_index.size();
		assert new_index == Configuration.subsystems_ordered.size();
		Configuration.subsystems_by_name.put(subsystem.getCode(), new_index);
		Configuration.subsystems_ordered.add(subsystem);
		Configuration.subsystems_index.put(subsystem, new_index);
		return new_index;
	}

	void
	setSubsystem(int config_index, ConfigSubsystem<?>.Config conf) {
		while (this.configurations.size() <= config_index) {
			this.configurations.add(null);
		}
		this.configurations.set(config_index, conf);
	}

	// Individual subsystems
	private static ConfigSubsystem<ParameterEvaluationOrder> SUBSYSTEM_parameter_evaluation_order = new ConfigSubsystem<>(
			"Parameter evaluation order",
			"PEO",
			ParameterEvaluationOrder.LeftToRight,
			ParameterEvaluationOrder.RightToLeft
			);
	public ConfigSubsystem<ParameterEvaluationOrder>.Config parameter_evaluation_order = SUBSYSTEM_parameter_evaluation_order.getConfig(this);

	private static ConfigSubsystem<ShortCircuitEvaluation> SUBSYSTEM_short_circuit_evaluation = new ConfigSubsystem<>(
			"Short Circuit Evaluation",
			"SCE",
			ShortCircuitEvaluation.ON,
			ShortCircuitEvaluation.OFF
			);
	public ConfigSubsystem<ShortCircuitEvaluation>.Config short_circuit_evaluation = SUBSYSTEM_short_circuit_evaluation.getConfig(this);

	private static ConfigSubsystem<OperandEvaluationOrder> SUBSYSTEM_operand_evaluation_order = new ConfigSubsystem<>(
			"Operand Evaluation Order",
			"OEO",
			OperandEvaluationOrder.LeftToRight,
			OperandEvaluationOrder.RightToLeft
			);
	public ConfigSubsystem<OperandEvaluationOrder>.Config operand_evaluation_order = SUBSYSTEM_operand_evaluation_order.getConfig(this);

	private static ConfigSubsystem<TypeNamesTYPE> SUBSYSTEM_type_names_TYPE = new ConfigSubsystem<>(
			"Type Names for TYPE",
			"TNT",
			TypeNamesTYPE.Structural,
			TypeNamesTYPE.Nominal
			);
	public ConfigSubsystem<TypeNamesTYPE>.Config type_names_TYPE = SUBSYSTEM_type_names_TYPE.getConfig(this);

	private static ConfigSubsystem<TypeCheck> SUBSYSTEM_type_check = new ConfigSubsystem<>(
			"Type Checking",
			"TC",
			TypeCheck.Dynamic,
			TypeCheck.Static,
			TypeCheck.None
			);
	public ConfigSubsystem<TypeCheck>.Config type_check = SUBSYSTEM_type_check.getConfig(this);

	private static ConfigSubsystem<ArrayAssignmentSemantics> SUBSYSTEM_array_assignment_semantics = new ConfigSubsystem<>(
			"Array Assignment",
			"AA",
			ArrayAssignmentSemantics.Copy,
			ArrayAssignmentSemantics.Reference
			);
	public ConfigSubsystem<ArrayAssignmentSemantics>.Config array_assignment = SUBSYSTEM_array_assignment_semantics.getConfig(this);

	private static ConfigSubsystem<ParameterPassingMode> SUBSYSTEM_parameter_passing_mode = new ConfigSubsystem<>(
			"Parameter Passing",
			"PP",
			ParameterPassingMode.ByValue,
			ParameterPassingMode.ByResult,
			ParameterPassingMode.ByValueResult,
			ParameterPassingMode.ByReference,
			ParameterPassingMode.ByName,
			ParameterPassingMode.ByNeed
			);
	public ConfigSubsystem<ParameterPassingMode>.Config parameter_passing = SUBSYSTEM_parameter_passing_mode.getConfig(this);

	private static ConfigSubsystem<VariableStorageBinding> SUBSYSTEM_variable_storage_binding = new ConfigSubsystem<>(
			"Locals Storage Binding",
			"LSB",
			VariableStorageBinding.StackDynamic,
			VariableStorageBinding.Static
			);
	public ConfigSubsystem<VariableStorageBinding>.Config variable_storage = SUBSYSTEM_variable_storage_binding.getConfig(this);

	private static ConfigSubsystem<ScopingMode> SUBSYSTEM_scoping_mode = new ConfigSubsystem<>(
			"Scoping",
			"S",
			ScopingMode.Static,
			ScopingMode.Dynamic
			);
	public ConfigSubsystem<ScopingMode>.Config scoping = SUBSYSTEM_scoping_mode.getConfig(this);

	private static ConfigSubsystem<ClosureEnvironmentBinding> SUBSYSTEM_closure_environment_binding = new ConfigSubsystem<>(
			"Environment Binding",
			"EB",
			ClosureEnvironmentBinding.Deep,
			ClosureEnvironmentBinding.Shallow
			);
	public ConfigSubsystem<ClosureEnvironmentBinding>.Config closure_env_binding = SUBSYSTEM_closure_environment_binding.getConfig(this);

	private static ConfigSubsystem<ArrayEquality> SUBSYSTEM_array_equality = new ConfigSubsystem<>(
			"Array Equality",
			"AEQ",
			ArrayEquality.Structural,
			ArrayEquality.Reference
			);
	public ConfigSubsystem<ArrayEquality>.Config array_equality = SUBSYSTEM_array_equality.getConfig(this);

	private static ConfigSubsystem<ProcedureSubtyping> SUBSYSTEM_procedure_arg_subtyping = new ConfigSubsystem<>(
			"Procedure Argument Subtyping",
			"PSA",
			ProcedureSubtyping.Invariant,
			ProcedureSubtyping.Covariant,
			ProcedureSubtyping.Contravariant,
			ProcedureSubtyping.Bivariant
			);
	public ConfigSubsystem<ProcedureSubtyping>.Config procedure_arg_subtyping = SUBSYSTEM_procedure_arg_subtyping.getConfig(this);

	private static ConfigSubsystem<ProcedureSubtyping> SUBSYSTEM_procedure_return_subtyping = new ConfigSubsystem<>(
			"Procedure Return Subtyping",
			"PSR",
			ProcedureSubtyping.Invariant,
			ProcedureSubtyping.Covariant,
			ProcedureSubtyping.Contravariant,
			ProcedureSubtyping.Bivariant
			);
	public ConfigSubsystem<ProcedureSubtyping>.Config procedure_return_subtyping = SUBSYSTEM_procedure_return_subtyping.getConfig(this);

	private static ConfigSubsystem<LiteralType> SUBSYSTEM_literal_type = new ConfigSubsystem<>(
			"Literal Number Type",
			"LT",
			LiteralType.Integer,
			LiteralType.Subrange
			);
	public ConfigSubsystem<LiteralType>.Config literal_type = SUBSYSTEM_literal_type.getConfig(this);

	private static ConfigSubsystem<UnknownType> SUBSYSTEM_unknown_type = new ConfigSubsystem<>(
			"Default Type (for ommitted type names)",
			"DT",
			UnknownType.Any,
			UnknownType.Error,
			UnknownType.Integer
			);
	public ConfigSubsystem<UnknownType>.Config unknown_type = SUBSYSTEM_unknown_type.getConfig(this);

	public boolean
	stronglyTyped() {
		return this.type_check.get().dynamic_checks() || this.type_check.get().static_checks();
	}

	// ==========

	/**
	 * Constructs a default configuration
	 */

	public Configuration() {
	}
	public void
	setSubsystem(String subsystem_code, String option_code) {
		if (!Configuration.subsystems_by_name.containsKey(subsystem_code)) {
			throw new IllegalArgumentException("Unknown subsystem `" + subsystem_code + "'");
		}
		int subsystem_index = Configuration.subsystems_by_name.get(subsystem_code);
		if (subsystem_index == OP_SUBSYSTEM_INDEX) {
			// special-case handling for the op subsystem
			if (option_code.length() != 2 * Op.values().length) {
				throw new IllegalArgumentException(OP_SUBSYSTEM_CODE + " configurations must describe precedence and associativity (e.g., `1r') for the following operators: " + Arrays.toString(Op.values()));
			}
			int index = 0;
			for (Op op : Op.values()) {
				String code = option_code.substring(index, index + 2);
				index += 2;
				this.op_config.put(op, OpConfig.parse(code));
			}
			return;
		}
		ConfigSubsystem<?>.Config config = this.configurations.get(subsystem_index);
		config.set(option_code);
	}

	public void
	setOptions(String config_string) {
		if (config_string.length() == 0) {
			return;
		}
		String[] args = config_string.split(",");
		for (String arg : args) {
			String[] tuple = arg.split(":");
			if (tuple.length != 2) {
				throw new IllegalArgumentException("Invalid configuration option `" + arg + "': expected `SUBSYSTEMNAME:OPTION'");
			}
			this.setSubsystem(tuple[0], tuple[1]);
		}
	}

	public static Configuration
	parse(String config_string) {
		Configuration config = new Configuration();
		config.setOptions(config_string);
		return config;
	}

	@Override
	public String
	toString() {
		StringBuffer output = new StringBuffer();

		// opconfig
		output.append(OP_SUBSYSTEM_CODE);
		output.append(":");
		for (Op op : Op.values()) {
			output.append(this.op_config.get(op).toString());
		}

		for (ConfigSubsystem<?> subsystem : Configuration.subsystems_ordered) {
			output.append(",");
			final int index = Configuration.subsystems_index.get(subsystem);
			final ConfigSubsystem<?>.Config subsystem_conf = this.configurations.get(index);
			output.append(subsystem.getCode());
			output.append(":");
			output.append(subsystem_conf.get().getCode());
		}

		return output.toString();
	}

	public static  List<? extends ConfigSubsystem<?>>
	getSubsystems() {
		return Configuration.subsystems_ordered;
	}

	public int
	getCallLimit() {
		return this.call_limit;
	}

	public int
	getStepLimit() {
		return this.step_limit;
	}

	public void
	setCallLimit(int limit) {
		this.call_limit = limit;
	}

	public void
	setStepLimit(int limit) {
		this.step_limit = limit;
	}
}
