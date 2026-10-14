
async function addStudent() {
    let name = document.getElementById("name").value.trim();
    let age = document.getElementById("age").value;

    if (name === "" || age === "") {
        alert("Please enter student details!");
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/api/students",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: new URLSearchParams({
                    name: name,
                    age: age
                })
            }
        );

        const message = await response.text();

        if (!response.ok) {
            alert("Error: " + message);
            return;
        }

        // SQL lo save ayyaka website list lo add chestundi
        let li = document.createElement("li");
        li.textContent = name + " - " + age + " years";

        document.getElementById("studentList").appendChild(li);

        document.getElementById("name").value = "";
        document.getElementById("age").value = "";

        alert("Student saved to MySQL successfully!");

    } catch (error) {
        alert("Java server connect avvaledu. Server running lo undo check cheyyi.");
        console.error(error);
    }
}
