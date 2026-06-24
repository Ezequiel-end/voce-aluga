variable "project_name" { type = string }
variable "subnet_id" { type = string }
variable "ami_id" { type = string }
variable "api_private_ip" { type = string }
variable "api_port" { type = number }
variable "ssh_public_key" { type = string }
variable "security_group_ids" { type = list(string) }

resource "aws_key_pair" "main" {
  key_name   = "${var.project_name}-key"
  public_key = var.ssh_public_key
}

resource "aws_instance" "api" {
  ami                    = var.ami_id
  instance_type          = "t3.micro"
  subnet_id              = var.subnet_id
  vpc_security_group_ids = var.security_group_ids
  key_name               = aws_key_pair.main.key_name
  private_ip             = var.api_private_ip

  tags = {
    Name = "${var.project_name}-api-ec2"
    Role = "api-rest"
  }
}

output "instance_id" {
  value = aws_instance.api.id
}

output "api_private_ip" {
  value = aws_instance.api.private_ip
}
