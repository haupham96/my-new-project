function handleChange(input, type) {
    let noImageUrl = 'https://t4.ftcdn.net/jpg/04/70/29/97/360_F_470299797_UD0eoVMMSUbHCcNJCdv2t8B2g1GVqYgs.jpg';
    let mainImageId = "#main-image";

    // Up ảnh đại diện
    if (type === 'single') {
        if (input.files && input.files.length === 1) {
            let reader = new FileReader();
            reader.onload = e => {
                $(mainImageId).attr('src', e.target.result);
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            $(mainImageId).attr('src', noImageUrl);
        }
    }
    // Up 3 ảnh chi tiết
    if (type === 'multiple') {
        let detailImages = $('#detail-images');
        let reader;
        if (input.files && input.files.length >= 1) {
            detailImages.empty();
            for (let i = 0; i < input.files.length; i++) {
                reader = new FileReader();
                reader.onload = e => {
                    let img = `<img src="${e.target.result}" width="100px" height="100px"  alt="product-img"/>`;
                    detailImages.append(img);
                }
                reader.readAsDataURL(input.files[i]);
            }
        } else {
            detailImages.empty();
        }
    }
}

setTimeout(() => {
    $("#textMessage").text("");
}, 2000);

