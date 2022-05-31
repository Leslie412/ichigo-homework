package main

import (
	"database/sql"
	"log"

	_ "github.com/go-sql-driver/mysql"
)

func main() {
	db, err := sql.Open("mysql", "ichigo:ichigo@tcp(127.0.0.1:3306)/ichigo_db")
	defer db.Close()

	if err != nil {
		log.Fatal(err)
	}

	sql := `WITH new_tiers AS (
		SELECT customer_id, 
		CASE
			WHEN SUM(total_in_cents) < 10000 THEN 'Bronze'
			WHEN SUM(total_in_cents) >= 10000 AND SUM(total_in_cents) < 50000 THEN 'Silver'
			WHEN SUM(total_in_cents) > 50000 THEN 'Gold'
		END tier
		FROM order_tbl WHERE YEAR(order_date) >= YEAR(CURDATE() + INTERVAL -1 YEAR) GROUP BY customer_id
	)
	UPDATE customer c, new_tiers n SET c.tier = n.tier, c.update_time = NOW() WHERE c.customer_id = n.customer_id`

	_, err = db.Exec(sql)
	if err != nil {
		log.Fatal(err)
	}
}
