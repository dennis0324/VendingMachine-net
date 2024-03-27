import ProductItem from "./ProductItem";

function ProductManager(props) {
  const { data, className } = props;
  const predefinedClass = "";
  const combineClass = [className].join(" ");

  return (
    <conatiner className={combineClass}>
      {data.map((item) => (
        <ProductItem product={item} />
      ))}
    </conatiner>
  );
}

export default ProductManager;
