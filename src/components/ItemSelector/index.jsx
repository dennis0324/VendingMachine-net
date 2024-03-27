import MenuItem from "./MenuItem";

function ItemSelector(props) {
  const { items, onSelect, className } = props;
  const preClassName =
    "grid col-span-2 max-sm:grid-cols-2 grid-cols-3 items-center gap-4 w-4/5";
  const combineClass = [className || "", preClassName].join(" ");
  console.log(items);
  return (
    <>
      <container className={combineClass}>
        {items.map((item) => (
          <MenuItem key={item.name} item={item} />
        ))}
      </container>
    </>
  );
}

export default ItemSelector;

// {/* <container className="grid grid-cols-3 justify-center items-center gap-4 w-4/5"> */}
// {/*   {data.map((e) => ( */}
// {/*     <Items name={e.name} price={e.price} /> */}
// {/*   ))} */}
// {/* </container> */}
