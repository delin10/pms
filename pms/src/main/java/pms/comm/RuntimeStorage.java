package pms.comm;

import pms.service.impl.BuildingService;
import pms.service.impl.ChargeFormService;
import pms.service.impl.ChargeItemService;
import pms.service.impl.CommunityService;
import pms.service.impl.ContractService;
import pms.service.impl.DepartmentService;
import pms.service.impl.EmployeeService;
import pms.service.impl.OwnerService;
import pms.service.impl.RoleService;
import pms.service.impl.RoomService;
import pms.service.impl.UserService;
import pms.util.auth.Getter;

/**
 * 此例采用在并发情况下的懒汉式单例模式 作者认为synchronized会对性能造成大量的性能损耗， 所以在外层嵌套了一层条件判断
 * 
 * @author delin
 *
 */
public class RuntimeStorage {
	public static String root="E:/";
	private static CommunityService communityService;
	private static BuildingService buildingService;
	private static ContractService contractService;
	private static DepartmentService departmentService;
	private static EmployeeService employeeService;
	private static OwnerService ownerService;
	private static RoleService roleService;
	private static RoomService roomService;
	private static UserService userService;
	private static ChargeItemService chargeItemService;
	private static ChargeFormService chargeFormService;
	private static String path = "D:/backup";
	private static Getter getter;

	static {
		getter = new Getter();
	}

	public static String getPath() {
		return path;
	}

	public static CommunityService getCommunityService() {
		if (communityService == null) {
			getCommunityService_();
		}
		return communityService;
	}

	private static synchronized CommunityService getCommunityService_() {
		if (communityService == null) {
			communityService = new CommunityService();
		}
		return communityService;
	}

	public static BuildingService getBuildingService() {
		if (buildingService == null) {
			getBuildingService_();
		}

		return buildingService;
	}

	public static ContractService getContractService() {
		if (contractService == null) {
			getContractService_();
		}
		return contractService;
	}

	private static synchronized ContractService getContractService_() {
		if (contractService == null) {
			contractService = new ContractService();
		}
		return contractService;
	}

	public static DepartmentService getDepartmentService() {
		if (departmentService == null) {
			getDepartmentService_();
		}
		return departmentService;
	}

	private static synchronized DepartmentService getDepartmentService_() {
		if (departmentService == null) {
			departmentService = new DepartmentService();
		}
		return departmentService;
	}

	public static EmployeeService getEmployeeService() {
		if (employeeService == null) {
			getEmployeeService_();
		}
		return employeeService;
	}

	private static synchronized EmployeeService getEmployeeService_() {
		if (employeeService == null) {
			employeeService = new EmployeeService();
		}
		return employeeService;
	}

	public static OwnerService getOwnerService() {
		if (ownerService == null) {
			getOwnerService_();
		}
		return ownerService;
	}

	private static synchronized OwnerService getOwnerService_() {
		if (ownerService == null) {
			ownerService = new OwnerService();
		}
		return ownerService;
	}

	public static RoleService getRoleService() {
		if (roleService == null) {
			getRoleService_();
		}
		return roleService;
	}

	private static synchronized RoleService getRoleService_() {
		if (roleService == null) {
			roleService = new RoleService();
		}
		return roleService;
	}

	public static RoomService getRoomService() {
		if (roomService == null) {
			getRoomService_();
		}
		return roomService;
	}

	private static synchronized RoomService getRoomService_() {
		if (roomService == null) {
			roomService = new RoomService();
		}
		return roomService;
	}

	public static UserService getUserService() {
		if (userService == null) {
			getUserService_();
		}
		return userService;
	}

	private static synchronized UserService getUserService_() {
		if (userService == null) {
			userService = new UserService();
		}
		return userService;
	}

	private static synchronized BuildingService getBuildingService_() {
		if (buildingService == null) {
			buildingService = new BuildingService();
		}
		return buildingService;
	}

	public static Getter getGetter() {
		if (getter == null) {
			getGetter_();
		}
		return getter;
	}

	private static synchronized Getter getGetter_() {
		if (getter == null) {
			getter = new Getter();
		}
		return getter;
	}

	public static ChargeItemService getChargeItemService() {
		if (chargeItemService == null) {
			getChargeItemService_();
		}
		return chargeItemService;
	}

	private static synchronized ChargeItemService getChargeItemService_() {
		if (chargeItemService == null) {
			chargeItemService = new ChargeItemService();
		}
		return chargeItemService;
	}
	
	public static ChargeFormService getChargeFormService() {
		if (chargeFormService == null) {
			getChargeFormService_();
		}
		return chargeFormService;
	}

	private static synchronized ChargeFormService getChargeFormService_() {
		if (chargeFormService == null) {
			chargeFormService = new ChargeFormService();
		}
		return chargeFormService;
	}
}