function IconHelper(props) {
  const { children } = props;
  return (
    <div className="m-2" {...props}>
      {children}
    </div>
  );
}

export default IconHelper;
