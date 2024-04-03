import ItemSelector from "../components/ItemSelector";
import CartManager from "../components/CartManager";
import PopupManager from "../components/PopupManager";

function Home() {
  return (
    <div className="App h-screen w-screen">
      <PopupManager />
      <div className="h-full flex flex-row max-sm:flex-col max-sm:py-0 py-5 justify-center items-center">
        <section className="max-sm:h-full max-sm:flex max-sm:flex-col max-sm:justify-between max-sm:mb-0 m-6 w-full grid grid-cols-6 justify-items-center items-center">
          <ItemSelector className={"col-span-4 mx-5"} />
          <CartManager className={"col-span-2"} />
        </section>
      </div>
    </div>
  );
}

export default Home;
