variable "project_name" { type = string }
variable "vpc_id" { type = string }
variable "subnet_ids" { type = list(string) }
variable "db_name" { type = string }
variable "db_username" { type = string }
variable "db_password" { type = string }
variable "use_localstack_rds" { type = bool }
variable "emulated_rds_host" { type = string }
variable "emulated_rds_port" { type = number }
variable "security_group_ids" { type = list(string) }

resource "aws_db_subnet_group" "main" {
  count = var.use_localstack_rds ? 1 : 0

  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = var.subnet_ids

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

resource "aws_db_instance" "main" {
  count = var.use_localstack_rds ? 1 : 0

  identifier              = "${var.project_name}-rds"
  engine                  = "mysql"
  engine_version          = "8.0"
  instance_class          = "db.t3.micro"
  allocated_storage       = 20
  db_name                 = var.db_name
  username                = var.db_username
  password                = var.db_password
  skip_final_snapshot     = true
  publicly_accessible     = false
  db_subnet_group_name    = aws_db_subnet_group.main[0].name
  vpc_security_group_ids  = var.security_group_ids
  backup_retention_period = 0

  tags = {
    Name = "${var.project_name}-rds"
    Role = "database"
  }
}

output "endpoint" {
  value = var.use_localstack_rds ? aws_db_instance.main[0].address : var.emulated_rds_host
}

output "port" {
  value = var.use_localstack_rds ? aws_db_instance.main[0].port : var.emulated_rds_port
}
