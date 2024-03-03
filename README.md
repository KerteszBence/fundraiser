Openapi for frontend:
- run the BackendApplication and open this link:
- http://localhost:8080/swagger-ui.html


Introduction:

Fundraiser, otherwise known as a fundraising website with the dynamics of Social Media combined. 
It is an independent platform where different donations and campaigns we present. 
Anyone can easily register and choose freely for themselves among interesting campaigns brought by creative people, i.e. creators created. 
Ordinary users are called Donors, who can support any campaign, they can follow their creators and send donations.

I solved the user registration in such a way that email confirmation is required, without it you can only browse the site, 
you can't start a campaign or donate, we also send an email to the user about the successful confirmation, 
and the management of the forgotten password has also been solved, also by sending an email.
From the side of security, method security and jwt security are built into the code, for example, the password is also stored securely.

When deleting users, I use events so that possible refunds are also appropriate.
For larger listings, I use pageable to save resources.
The program has its own error handling written for all foreseeable errors, they are also validated in the dtos so that the data can only be entered correctly, 
which always returns an error message or error messages in case of wrong data entry. This applies to both creations and modifications.

The Cloudinary api is integrated, with the help of which files (for example images, videos, pdfs) can be uploaded to the cloudinary hosting provider, 
and then they can be accessed from there, for example, as a profile picture, campaign video, image, or file. When a file is deleted, 
the files are not only deleted from our own database, but also from cloudinary's storage space, thus avoiding overloading of cloudinary's storage space. 
To reduce additional resource requirements. 

The paypal api is also integrated, with the help of which real money transfers can be realized, it works fully automatically, 
if the campaign reaches the amount set as the goal, the amount is automatically allocated to the creator who started the campaign and the campaign is closed. 
When the campaign expires and did not reach the set goal, the amount will be automatically returned to the donors and the campaign will also be closed. 
All money movements are recorded in a donation table for traceability, on the basis of which, for example, we also start automated transfers.

The fixer api is also integrated, with the help of which the exchange between currencies and the automatic exchange rate query are realized and it cooperates with paypal.

The program is prepared to run on the production server.