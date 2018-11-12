package pms.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pms.bean.Building;
import pms.bean.Car;
import pms.bean.Community;
import pms.bean.Company;
import pms.bean.Contract;
import pms.bean.Department;
import pms.bean.Employee;
import pms.bean.Owner;
import pms.bean.Owner_family;
import pms.bean.Pet;
import pms.bean.Room;
import pms.bean.Server_contract;
import pms.util.auth.Getter;
import pms.util.auth.bean.Role;
import pms.util.auth.bean.User;
import pms.util.auth.manager.RoleManager;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.comm.KV_Obj;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;
import pms.util.db.DBUtil.SQL;
import pms.util.file.FileUtil;
import pms.util.reflect.Reflector;
import pms.view.UserView;

public class Dao {
	private static byte[] default_image = new byte[0];
	//public static Getter get = new Getter();
	public static String path = "D:/backup";

	public static Info company_image() {
		Info info = new Info();
		Company company = (Company) DBUtil.parse(DBUtil.queryAll("company", null),
				Company.class);
		System.out.println(company);
		info.setData(company == null ? default_image : company.getImgUrl());
		// System.out.println(info.getData());
		return info;
	}

	public static Info setRole(String user_id, String role_id) {
		return Getter.rolem.auth(user_id, role_id);
	}

	public static Info auth(Session session, ArrayList<String> resource_ids, String role_id) {
		return Getter.am.auth(session, resource_ids, role_id);
	}

	/**
	 * BUILDING_ID NOT NULL VARCHAR2(3) COMMUNITY_NAME NOT NULL VARCHAR2(20) ROOM_ID
	 * NOT NULL VARCHAR2(4) OWNER_ID NOT NULL VARCHAR2(18) FLOOR_ID
	 * 
	 * @param id
	 * @param pwd
	 * @return
	 */
	public static Info login(String id, String pwd) {
		return Getter.um.login(id, pwd, null, args -> {
			Session session = (Session) args[0];
			User user = (User) session.getAttributes("user");
			String role_id = session.getAttributes("role").toString();
			String rel_id = user.getRel_id();
			Role role = (Role) Getter.rolem.getRole(role_id).getData();
			Map<String, String> mapper = new HashMap<>();
			Map<String, Object> maps = new HashMap<>();
			if (!RoleManager.SYS.equals(role_id)) {
				mapper.put("community_name", "community");
				mapper.put("room_id", "room");
				mapper.put("floor_id", "floor");
				mapper.put("building_id", "building");
				maps = DBUtil.rsToMap(DBUtil.query(SQL.create()
						.select(new KV("owner", "community_name").unwrap(), new KV("owner", "room_id").unwrap(),
								new KV("owner", "building_id").unwrap(), new KV("owner", "floor_id").unwrap())
						.from("owner").left_join("users")
						.on(new Keys().start(new KV("owner.owner_id", "users.rel_id").unwrap()))
						.where(new Keys().start(new KV("owner.owner_id", rel_id))).complete()), mapper);
			}
			maps.put("role", role.getDescription());
			return maps;
		});
	}

	public static Info logout(Session session) {
		Info info = new Info();
		Getter.sm.expired(session.getSession_id());
		info.suc("success");
		return info;
	}

	public static Info resources_tree(Session session) {
		Info info = new Info();
		KV_Obj kv = KV_Obj.create().setAttr("url").setComparator((a, b) -> {
			return a.equals(b) ? 0 : -1;
		}).setSrc("");

		info.setData(Getter.um.resources_where(session.getAttributes("role").toString(), kv));
		info.suc("sucess");
		return info;
	}

	public static Info showUsers() {
		Info info = new Info();
		ArrayList<Object> ls = DBUtil
				.toArrayList(
						DBUtil.query((SQL.create().select(UserView.class).from("users").left_join("users_roles")
								.on(new Keys().start(new KV("users.id", "user_id").unwrap())).left_join("roles")
								.on(new Keys().start(new KV("roles.id", "role_id").unwrap()))).complete()),
						UserView.class);
		info.setData(ls);
		info.suc("success");
		return info;
	}

	public static Info search(String subject, String key, String word) {
		Info info = new Info();
		info.suc("success");
		if ("Roles".equalsIgnoreCase(subject)) {
			info.setData(DBUtil.search(subject, key, word, Role.class));
		} else if ("Users".equalsIgnoreCase(subject)) {
			info.setData(DBUtil.toArrayList(
					DBUtil.query(SQL.parse(Reflector.getSQL(UserView.class))
							.and_like(Reflector.get_db_col(key, UserView.class).getCol(), word).complete()),
					UserView.class));
		} else {
			info.fail("fail");
		}

		return info;
	}

	public static Info community_names() {
		Info info = new Info();
		Map<String, String> mapper = new HashMap<>();
		mapper.put("name", "name");
		String sql = SQL.create().select(new KV("community", "name").unwrap()).from("community").complete();
		// System.out.println(sql);
		String[] names = DBUtil.rsToMapList(DBUtil.query(sql), mapper).stream().map(kv -> kv.get("name").toString())
				.toArray(String[]::new);
		// System.out.println("社区名字"+names);
		info.setData(names);
		info.suc("success");
		return info;
	}

	public static Info addRole(Role role) {
		role.setId(Getter.rolem.generateId());
		role.setAvailable("1");
		return Getter.rolem.addRole(role);
	}

	public static Info updateRole(Role role) {
		return Getter.rolem.updateRole(role);
	}

	public static Info delRole(String role_id) {
		return Getter.rolem.delRole(role_id);
	}

	public static Info roles() {
		return Getter.rolem.roles();
	}

	public static Info resources_tab(Session session, String fid) {
		Info info = new Info();
		KV_Obj kv_a = KV_Obj.create().setAttr("url").setComparator((a, b) -> {
			return a.equals(b) ? -1 : 0;
		}).setSrc("");

		KV_Obj kv_b = KV_Obj.create().setAttr("fid").setComparator((a, b) -> {
			return a.equals(b) ? 0 : -1;
		}).setSrc(fid);

		info.setData(Getter.um.resources_where(session.getAttributes("role").toString(), kv_a, kv_b));
		info.suc("sucess");
		return info;
	}

	public static Info allResources(String role_id) {
		Info info = new Info();
		HashMap<String, Object> map = new HashMap<>();
		map.put("all", Getter.am.all());
		map.put("own", Getter.um.resources(role_id));
		info.setData(map);
		info.suc("success");
		return info;
	}

	public static Info resources(Session session) {
		Info info = new Info();
		HashMap<String, Object> data = new HashMap<>();
		data.put("resources", session.getAttributes("resources"));
		data.put("role", session.getAttributes("role"));
		info.setData(data);
		info.suc("success");
		return info;
	}

	public static Info backups() {
		Info info = new Info();
		info.setData(FileUtil.recursiveFileIn(path));
		info.suc("success");
		return info;
	}

	public static Info backup() {
		Info info = new Info();
		DBUtil.backup_exp(path, 1024);
		info.suc("success");
		return info;
	}

	public static Info delBackup(ArrayList<String> list) {
		Info info = new Info();
		info.suc("删除结果:");
		list.stream().forEach(path -> {
			boolean res = FileUtil.deleteFile(path);
			info.line();
			if (res) {
				info.append("成功刪除文件:" + path);
			} else {
				info.append("刪除失敗" + path);
			}
		});

		return info;
	}

	public static Info restore(String path) {
		Info info = new Info();
		DBUtil.restore_imp(path, 1024);
		info.suc("success");
		return info;
	}

	public static boolean addCompanyInfo(Company com) {
		return DBUtil.insertObject(com, com.getClass(), "Company");
	}

	public static boolean addCommunity(Community comm) {
		return DBUtil.insertObject(comm, comm.getClass(), "Community");
	}

	public static boolean addBuiling(Building building) {
		return DBUtil.insertObject(building, building.getClass(), "Building");
	}

	public static boolean addRoom(Room room) {
		return DBUtil.insertObject(room, room.getClass(), "Room");
	}

	public static boolean addOwner(Owner owner) {
		return DBUtil.insertObject(owner, owner.getClass(), "Owner");
	}

	public static boolean addContract(Contract c) {
		return DBUtil.insertObject(c, c.getClass(), "Contract");
	}

	public static boolean addPet(Pet p) {
		return DBUtil.insertObject(p, p.getClass(), "Pet");
	}

	public static boolean addCar(Car c) {
		return DBUtil.insertObject(c, c.getClass(), "Car");
	}

	public static boolean addOwnerFamily(Owner_family family) {
		return DBUtil.insertObject(family, family.getClass(), "Owner_family");
	}

	public static boolean addDepartment(Department d) {
		return DBUtil.insertObject(d, d.getClass(), "Department");
	}

	public static boolean addEmployee(Employee e) {
		return DBUtil.insertObject(e, e.getClass(), "Employee");
	}

	public static boolean addServer_contract(Server_contract sc) {
		return DBUtil.insertObject(sc, sc.getClass(), "Server_contract");
	}

	public static Company queryCompany() {
		return (Company) DBUtil.parse(DBUtil.queryAll("Company", null), Company.class);
	}

	public static ArrayList<Object> queryCommunity() {
		return DBUtil.toArrayList(DBUtil.queryAll("Community", null), Community.class);
	}

	// 处理building
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> queryBuilding(Session session) {
		// 获取用户登录基础信息
		Map<String, Object> basic = (Map<String, Object>) session.getAttributes("_basic_");
		String community_name = (String) basic.get("community");
		System.out.println(community_name);
		if (community_name == null)
			return DBUtil.toArrayList(DBUtil.queryAll("Building", null), Building.class);
		else
			return DBUtil.toArrayList(DBUtil.keyQuery("Building", new KV("community_name", community_name).op("=")),
					Building.class);
	}

	// 删除building
	public static Info deleteBuilding(Map<String, String>[] maps) {
		Info info = new Info();
		info.suc("");
		Arrays.stream(maps).forEach(map -> {
			String community_name = map.get("community_name");
			String building_id = map.get("building_id");
			if (community_name == null || building_id == null) {
				info.append(String.format("[%s,%s]", community_name, building_id) + "  failed(原因:参数不存在);");
				return;
			}
			boolean res = DBUtil.keyDel("building",
					new Keys().start(new KV("community_name", community_name)).and(new KV("building_id", building_id)));
			if (res) {
				info.append(String.format("[%s,%s]", community_name, building_id) + "  success;");
			} else {
				info.append(String.format("[%s,%s]", community_name, building_id) + "  failed;");
			}
		});
		return info;
	}

	public static ArrayList<Object> queryRoom(Session session) {
		@SuppressWarnings("unchecked")
		Map<String, Object> _basic_ = (Map<String, Object>) session.getAttributes("_basic_");
		String community_name = (String) _basic_.get("community");
		if (community_name == null) {
			return DBUtil.toArrayList(DBUtil.queryAll("room", null), Room.class);
		} else {
			SQL sql = SQL.create().select(new KV("room", "*").unwrap()).from("room");
			Keys keys = new Keys().start(new KV("community", community_name));
			sql.where(keys);
			return DBUtil.toArrayList(DBUtil.query(sql.complete()), Room.class);
		}
	}

	public static ArrayList<Object> queryOwner(Session session, String owner_name) {
		@SuppressWarnings("unchecked")
		Map<String, Object> _basic_ = (Map<String, Object>) session.getAttributes("_basic_");
		String community_name = (String) _basic_.get("community");
		if (community_name == null) {
			return DBUtil.toArrayList(DBUtil.queryAll("Owner", null), Owner.class);
		} else {
			SQL sql = SQL.create().select(new KV("community", "*").unwrap()).from("community");
			Keys keys = new Keys().start(new KV("COMMUNITY_NAME", community_name));
			if (owner_name != null) {
				keys.and(new KV("owner_name", owner_name));
			}
			return DBUtil.toArrayList(DBUtil.query(sql.complete()), Community.class);
		}
	}

	public static boolean updateCompany(Company company) {
		return DBUtil.updateObject(company, "company", new Keys().start(new KV("name", company.getInfo())));
	}

	public static boolean updateCommunity(Community community) {
		return DBUtil.updateObject(community, "community", new Keys().start(new KV("name", community.getName())));
	}

	public static boolean updateBuilding(Building building) {
		return DBUtil.updateObject(building, "building",
				new Keys().start(new KV("building_id", building.getBuilding_id()))
						.and(new KV("community_name", building.getCommunity_name())));
	}

	public static boolean updateRoom(Room room) {
		// System.out.println(JSON.toJSONString(room));
		return DBUtil.updateObject(room, "room",
				new Keys().start(new KV("building_id", room.getBuilding_id()))
						.and(new KV("community_name", room.getCommunity_name()))
						.and(new KV("room_id", room.getRoom_id())).and(new KV("floor_id", room.getFloor_id())));
	}

	public static boolean updateOwner(Owner owner) {
		return DBUtil.updateObject(owner, "owner",
				new Keys().start(new KV("building_id", owner.getBuilding_id()))
						.and(new KV("community_name", owner.getCommunity_name()))
						.and(new KV("room_id", owner.getRoom_id())).and(new KV("owner_id", owner.getOwner_id())));
	}

	public static boolean updateOwnerFamily(Owner_family owner_family) {
		return DBUtil.updateObject(owner_family, "owner_family",
				new Keys().start(new KV("owner_id", owner_family.getOwner_id()))
						.and(new KV("member_id", owner_family.getMember_id())));
	}

	public static boolean updateDepartment(Department department) {
		return DBUtil.updateObject(department, "department",
				new Keys().start(new KV("community_name", department.getCommunity_name()))
						.and(new KV("name", department.getName())));
	}

	public static boolean updateEmployee(Employee employee) {
		return DBUtil.updateObject(employee, "employee", new Keys().start(new KV("eid", employee.getEid())));
	}

	public static boolean updateServerContract(Server_contract server_contract) {
		return DBUtil.updateObject(server_contract, "server_contract",
				new Keys().start(new KV("contract_id", server_contract.getContract_id())));
	}

	public static boolean updatePet(Pet pet) {
		return DBUtil.updateObject(pet, "pet",
				new Keys().start(new KV("owner_id", pet.getOwner_id())).and(new KV("pet_id", pet.getPet_id())));
	}

	public static boolean updateCar(Car car) {
		return DBUtil.updateObject(car, "car",
				new Keys().start(new KV("owner_id", car.getOwner_id())).and(new KV("car_id", car.getCar_id())));
	}

	public static boolean updateContract(Contract contract) {
		return DBUtil.updateObject(contract, "contract",
				new Keys().start(new KV("contract_id", contract.getContract_id())));
	}

	/*
	 * public static void deleteBuilding(String community_name,String building_id) {
	 * DBUtil.keyDel("Building", new Keys().start(new
	 * KV("building_id",building_id)).and(new KV("community_name",community_name)));
	 * }
	 * 
	 * public static void deleteRoom(String community_name,String building_id,String
	 * room_id,int floor_id) { DBUtil.keyDel("Room", new Keys().start(new
	 * KV("building_id",building_id)).and(new
	 * KV("community_name",community_name)).and(new KV("room_id",room_id)).and(new
	 * KV("floor_id",floor_id))); }
	 * 
	 * public static void deleteOwner(String community_name,String
	 * building_id,String room_id,String owner_id) { DBUtil.keyDel("Owner", new
	 * Keys().start(new KV("building_id",building_id)).and(new
	 * KV("community_name",community_name)).and(new KV("room_id",room_id)).and(new
	 * KV("owner_id",owner_id))); }
	 * 
	 * public static void deleteOwnerFamily(String Owner_id,String member_id) {
	 * DBUtil.keyDel("Owner_family", new Keys().start(new
	 * KV("Owner_id",Owner_id)).and(new KV("member_id",member_id))); }
	 */

	public static boolean deleteDepartment(String community_name, String name) {
		return DBUtil.keyDel("Department",
				new Keys().start(new KV("community_name", community_name)).and(new KV("name", name)));
	}

	public static boolean deleteEmployee(String eid) {
		return DBUtil.keyDel("Employee", new KV("eid", eid));
	}

	public static boolean deletePet(String owner_id, String pet_id) {
		return DBUtil.keyDel("Pet", new Keys().start(new KV("owner_id", owner_id)).and(new KV("pet_id", pet_id)));
	}

	public static boolean deleteCar(String owner_id, String car_id) {
		return DBUtil.keyDel("Car", new Keys().start(new KV("owner_id", owner_id)).and(new KV("car_id", car_id)));
	}

	public static boolean deleteContract(String contract_id) {
		return DBUtil.keyDel("Contract", new KV("contract_id", contract_id));
	}

	public static boolean deleteServerContract(String contract_id) {
		return DBUtil.keyDel("server_contract", new KV("contract_id", contract_id));
	}
	
	public static void main(String[] args) throws Exception {
		byte[] bytes=(byte[]) Dao.company_image().getData();
		System.out.println(bytes.length);
		FileUtil.writeBytes(bytes, "E:/q.png");
	}
}
