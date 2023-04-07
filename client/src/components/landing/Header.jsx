import { Image,  Space} from "antd";
import { Link } from "react-router-dom";
import '../../App.css'


function Header() {

  return (
    <div className="AppHeader mx-5 grid grid-cols-2">
      <div>
        <Image
          width={150}
          src={require("./TranscendaLogo.png")}
          alt="TranscendaLogo"
          className="mt-5"
        ></Image>
      </div>
      <Space className="ml-96">
        <Link
          type="button"
          class="text-white bg-[#D1C2D8] hover:bg-white hover:text-[#66347F] font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 ml-96"
          to="/Login"
        >
           Log In
        </Link>

      </Space>
    </div>
  );
}
export default Header;