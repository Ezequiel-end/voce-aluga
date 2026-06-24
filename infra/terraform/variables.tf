variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "localstack_endpoint" {
  type    = string
  default = "http://localhost:4566"
}

variable "project_name" {
  type    = string
  default = "voce-aluga"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "api_private_ip" {
  type    = string
  default = "10.0.4.10"
}

variable "api_port" {
  type    = number
  default = 8081
}

variable "web_port" {
  type    = number
  default = 8082
}

variable "db_name" {
  type    = string
  default = "db_voce_aluga"
}

variable "db_username" {
  type    = string
  default = "root"
}

variable "db_password" {
  type    = string
  default = "senha123"
}

variable "emulated_rds_host" {
  type    = string
  default = "10.0.3.10"
}

variable "emulated_rds_port" {
  type    = number
  default = 3306
}

variable "use_localstack_rds" {
  type    = bool
  default = false
}

variable "kubeconfig_path" {
  type    = string
  default = "~/.kube/config"
}

variable "k8s_namespace" {
  type    = string
  default = "voce-aluga"
}

variable "ami_id" {
  type    = string
  default = "ami-00000000"
}

variable "gateway_port" {
  type    = number
  default = 8080
}

variable "gateway_host" {
  type    = string
  default = "10.0.0.11"
}
