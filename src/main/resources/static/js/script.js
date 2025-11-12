console.log("this is script file")

const toggleSidebar=()=>{
	
	if($(".sidebar").is(":visible")) {
		//true
		//close it
		$(".sidebar").css("display","none");
		$(".content").css("margin-left", "0%");
		//$(".fa-bars").show(); // show bars again
		//localStorage.setItem("sidebarOpen", "false"); // save state
		
	} else {
		//false
		//not show
		//$(".fa-bars").hide(); // hide bars
		$(".sidebar").css("display","block");
		$(".content").css("margin-left", "20%");
		//localStorage.setItem("sidebarOpen", "true"); // save state
	}
};

// ✅ Restore sidebar state on page load
/*$(document).ready(() => {
  const isOpen = localStorage.getItem("sidebarOpen") === "true";
  
  if (isOpen) {
    $(".sidebar").show();
    $(".content").css("margin-left", "20%");
    $(".fa-bars").hide();
  } else {
    $(".sidebar").hide();
    $(".content").css("margin-left", "0%");
    $(".fa-bars").show();
  }
});*/


const search = () => {
	//console.log("Searching....")
	
	let query=$("#search-input").val()
	
	
	if(query=="")
	{
		$(".search-result").hide();	
		
	} else{
		//search
		//console.log(query);
		
		//sending request to server
		
		let url=`http://localhost:8080/search/${query}`;
		
		fetch(url).then((response)=>{
			return response.json();
		})
		.then((data) => {
			
			//data ......
			//console.log(data);
			
			let text= `<div class='list-group'>`
			
				data.forEach((contact) => {
					
					text+= `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action'> ${contact.name} </a>`
					
				});
			
			text+=`</div>`;
			
			$(".search-result").html(text);
			$(".search-result").show();
			
		});
		
		
	}
}

//first request to server to create order

const paymentStart = () => {
	console.log("payment started");
	let amount=$("#payment_field").val();
	console.log(amount);
	if (amount == "" || amount == null) {

		swal("Failed !!", "Amount is required !!", "error");
		return;
	}
	
	//code
	//we will use ajax to send request to server to create order - use jquery ajax
	
	$.ajax(
		{
			url: '/user/create_order',
			data: JSON.stringify({amount: amount, info: 'order_request'}),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(response) {
				//invoked when success
				console.log(response);
				if(response.status == "created"){
					//open payment form
					 let options = {
						key:'rzp_test_RdcK0hyEGEaq5u',
						amount: response.amount,
						currency: 'INR',
						name: 'Smart Contact Manager',
						description: 'Donation',
						image: 'https://learncodewithdurgesh.com/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Flcwd_logo.45da3818.png&w=1080&q=75',
						order_id: response.id,
						handler: function(response){
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log('payment successful');

							updatePaymentOnServer(response.razorpay_payment_id, 
								response.razorpay_order_id,
								 "paid"
								);

						},
						prefill: {
       					 name: "",
       					 email: "",
        				 contact: ""
    					},
    					 notes: {
        				 address: "Hudkeshwar Road Nagpur"
						 },
 
    					 theme: {
       					  color: "#35b25cff"
   						 }

					 };

					 let rzp = new Razorpay(options);

					rzp.on('payment.failed', function (response){
        			console.log(response.error.code);
        			console.log(response.error.description);
       				console.log(response.error.source);
        			console.log(response.error.step);
        			console.log(response.error.reason);
        			console.log(response.error.metadata.order_id);
        			console.log(response.error.metadata.payment_id);
					//alert ("Oops! Payment Failed!");
					swal("Failed !!", "Oops! Payment Failed!", "error");
				});

					 rzp.open();

				}
		},
		error: function(error){
			//invoked when error
			console.log(error);
			alert("something went wrong");
		}

	}
	)
};


//
function updatePaymentOnServer(payment_id, order_id, status)
{

	$.ajax({
		url: '/user/update_order',
			data: JSON.stringify({payment_id: payment_id, 
									order_id: order_id, 
									status: status}),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success : function(response) {
				swal("Good job!", "Congratulations! Payment Successful!", "success");
			},
			error: function(error){
				swal("Failed !!", "Your payment is successfull, but we did not get on server, we will update you", "error");
			},
	});


}












