import ProductManager from "../components/ProductManager";

function Admin() {
  return (
    <div className="App h-screen w-screen">
      <section className="grid grid-cols-6 h-full">
        <ProductManager className={"col-span-2"} />
        {/* <LogManager className={"col-span-2"} /> */}
        {/* <ProductManager className={"col-span-2"} /> */}
      </section>
    </div>
  );
}

export default Admin;
